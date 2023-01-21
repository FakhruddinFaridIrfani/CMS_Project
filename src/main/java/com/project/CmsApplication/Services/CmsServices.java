package com.project.CmsApplication.Services;

import com.jcraft.jsch.*;
import com.project.CmsApplication.Utility.DateFormatter;
import com.project.CmsApplication.model.*;
import com.project.CmsApplication.repository.*;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;

import java.sql.SQLException;
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class CmsServices {

    private static final int BUFFER_SIZE = 8192;
    Gson gson = new Gson();
    DateFormatter dateFormatter = new DateFormatter();

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    PromoRepository promoRepository;

    @Autowired
    RunningTextRepository runningTextRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceMonitoringLogRepository deviceMonitoringLogRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    PlaylistResourceRepository playlistResourceRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RoleRepository roleRepository;
//
//    @Autowired
//    UserRoleRepository userRoleRepository;

    @Autowired
    PrivilegeRepository privilegeRepository;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    CmsEncryptDecrypt cmsEncryptDecrypt;

    @Autowired
    ConfigurationRepository configurationRepository;


//    @Autowired
//    @Qualifier("entityManagerFactory")
//    private EntityManager entityManager;

    Logger logger = LoggerFactory.getLogger(CmsServices.class);

    @Value("${attachment.path.promo}")
    private String attachmentPathPromo;

    @Value("${attachment.path.playlist}")
    private String attachmentPathPlaylist;

    @Value("${attachment.path.resource}")
    private String attachmentPathResource;

    @Value("${sftp.user.name}")
    private String sftpUser;

    @Value("${sftp.user.password}")
    private String sftpPassword;

    @Value("${sftp.url}")
    private String sftpUrl;


    //ROLE SECTION
    public BaseResponse<String> addNewRole(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        String role_name;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            role_name = jsonInput.optString("role_name");
            //Existing Role Name check
            List<Role> roleNameCheckResult = roleRepository.getRoleByName(role_name);
            if (roleNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Role name already exist / used");
                return response;
            }

            //Save object role
            Role role = new Role();
            role.setRole_name(role_name);
            role.setStatus("active");
            role.setCreated_by(userOnProcess);
            role.setCreated_date(new Date());
            role.setUpdated_by("");
            role.setUpdated_date(new Date());
            roleRepository.save(role);

            //SET DEFAULT PRIVILEGE TO INSERTED ROLE
            Role roleInserted = roleRepository.getRoleByName(jsonInput.optString("role_name")).get(0);
            int roleIdInserted = roleInserted.getRole_id();
            privilegeRepository.insertGeneralMenuName(roleIdInserted);

            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Role successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<List<Role>> getRoleList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String role_name;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            role_name = "%" + jsonInput.optString("role_name") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Role> getRoleResult = roleRepository.getRoleList(role_name, status, created_by, created_date, updated_by, updated_date);

            response.setData(getRoleResult);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Role Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Role> updateRole(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            //Existing Role Name check
            List<Role> roleNameCheckResult = roleRepository.getRoleByNameExceptId(jsonInput.optString("role_name"), jsonInput.optInt("role_id"));
            if (roleNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Role name already exist / used");
                return response;
            }
            roleRepository.updateRole(jsonInput.optString("role_name"), jsonInput.optString("status"),
                    userOnProcess, jsonInput.optInt("role_id"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Role successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Role> deleteRole(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            List<Users> usedRoleOnUser = usersRepository.getUserByRoleId(jsonInput.optInt("role_id"));
            if (usedRoleOnUser.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("The role still used by " + usedRoleOnUser.size() + " user(s)");
                return response;
            }

            roleRepository.deleteRole(jsonInput.optInt("role_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Role successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Role getRoleById(int role_id) {

        List<Role> roleList = roleRepository.getRoleById(role_id);
        String role_name = roleList.get(0).getRole_name();

        return roleList.get(0);
    }

    //PRIVILEGE
    public BaseResponse<List<Map<String, Object>>> getAllPrivilege(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List result = new ArrayList();
        JSONObject jsonInput;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                logger.info("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            List<Role> getAllRole = roleRepository.getRole();

            for (int i = 0; i < getAllRole.size(); i++) {
                Map<String, Object> resultMap = new HashMap<>();
                List<Privilege> privileges = privilegeRepository.getPrivilegeByRoleId(getAllRole.get(i).getRole_id());
                List<String> menuNames = new ArrayList();
                String menu_name = privileges.get(0).getMenu_name();
                menu_name = menu_name.replace("[", "").replace("]", "");
                String[] menuArray = menu_name.split(",");
                menuNames = Arrays.asList(menuArray);
                resultMap.put("Role", getAllRole.get(i));
                resultMap.put("Privilege", menuNames);
                result.add(resultMap);
            }

            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Privilege Listed");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Privilege> updatePrivilege(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        String menu_name;
        int role_id;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("401");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            menu_name = jsonInput.optString("menu_name");
            role_id = jsonInput.optInt("role_id");

            privilegeRepository.updatePrivilegeMenuName(menu_name, userOnProcess, role_id);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Previlege successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    //USERS SECTION
    public BaseResponse<String> addNewUsers(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        int branch_id;
        int region_id;
        int company_id;
        int role_id;
        String user_name;
        String user_password;
        String user_email;
        String user_full_name;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            String userToken = Long.toHexString(new Date().getTime());
            branch_id = jsonInput.optInt("branch_id");
            region_id = jsonInput.optInt("region_id");
            company_id = jsonInput.optInt("company_id");
            role_id = jsonInput.optInt("role_id");
            user_name = jsonInput.optString("user_name");
            user_password = jsonInput.optString("user_password");
            user_email = jsonInput.optString("user_email");
            user_full_name = jsonInput.optString("user_full_name");

            //user_name check
            List<Users> userNameCheckResult = usersRepository.getUsersByName(user_name);
            if (userNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("User name already exist / used");
                return response;
            }

            if (role_id == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("User role can't be empty");
                return response;
            }

            usersRepository.save(user_name, user_password, user_email, user_full_name, userOnProcess, userToken, branch_id, region_id, company_id, role_id);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("User successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getUsers(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String branch_id;
        String region_id;
        String company_id;
        String role_id;
        String user_name;
        String user_email;
        String user_full_name;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            user_name = "%" + jsonInput.optString("user_name") + "%";
            user_email = "%" + jsonInput.optString("user_email") + "%";
            user_full_name = "%" + jsonInput.optString("user_full_name") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            role_id = jsonInput.optInt("role_id") + "";
            if (role_id.compareToIgnoreCase("null") == 0 || role_id.compareToIgnoreCase("0") == 0) {
                role_id = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Users> getUserResult = usersRepository.getUsersList(user_name, user_email, status, user_full_name, created_by, created_date, updated_by, updated_date, branch_id, region_id, company_id, role_id);
            for (int i = 0; i < getUserResult.size(); i++) {
                Map<String, Object> resultMap = new HashMap<>();
                List<Role> roles = roleRepository.getRoleById(getUserResult.get(i).getRole_id());
                resultMap.put("User", getUserResult.get(i));
                resultMap.put("Role", roles.get(0));
                result.add(resultMap);
            }


            for (int i = 0; i < getUserResult.size(); i++) {
                getUserResult.get(i).setUser_password("null");
            }
            response.setData(result);

            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("User Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Users> updateUsers(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
//            List<Users> userNameCheckResult = usersRepository.getUsersByNameExceptId(jsonInput.optString("user_name"), jsonInput.optInt("user_id"));
//            if (userNameCheckResult.size() > 0) {
//                response.setStatus("500");
//                response.setSuccess(false);
//                response.setMessage("User name already exist / used");
//                return response;
//            }
            String userOnProcess = auth.get("user_name").toString();
            if (jsonInput.optInt("role_id") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("User role can't be empty");
                return response;
            }

            usersRepository.updateUser(jsonInput.optString("user_email"),
                    jsonInput.optString("status"), jsonInput.optString("user_full_name"),
                    userOnProcess, jsonInput.optInt("branch_id"),
                    jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optInt("role_id"), jsonInput.optInt("user_id"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("User successfully Updated");


        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Users> deleteUsers(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            usersRepository.deleteUser(jsonInput.optInt("user_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("User successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Map<String, Object>> loginUser(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        Map<String, Object> result = new HashMap<>();
        List<Users> dataLoginUser;

        try {
            JSONObject jsonInput = new JSONObject(input);
            //user status check
            List<Users> userNameCheckResult = usersRepository.getUsersByName(jsonInput.optString("user_name"));
            if (userNameCheckResult.size() > 0) {
                if (userNameCheckResult.get(0).getStatus().compareToIgnoreCase("active") != 0) {
                    response.setStatus("500");
                    response.setSuccess(false);
                    response.setMessage("Failed to login. User no longer exist");
                    return response;
                }
            }
            dataLoginUser = usersRepository.loginUser(jsonInput.optString("user_name"), jsonInput.optString("user_email"), jsonInput.optString("user_password"));
            if (dataLoginUser.size() == 0) {
                response.setStatus("401");
                response.setSuccess(false);
                response.setMessage("Failed to login. wrong User Name, Email, or Password");
                return response;
            }

            for (int i = 0; i < dataLoginUser.size(); i++) {
                List<Role> roles = roleRepository.getRoleById(dataLoginUser.get(i).getRole_id());
                List<Privilege> privileges = privilegeRepository.getPrivilegeByRoleId(roles.get(0).getRole_id());
                List<String> menuNames = new ArrayList();
                String menu_name = privileges.get(0).getMenu_name();
                menu_name = menu_name.replace("[", "").replace("]", "");
                String[] menuArray = menu_name.split(",");
                menuNames = Arrays.asList(menuArray);
                result.put("User", dataLoginUser.get(i));
                result.put("Role", roles.get(0));
                result.put("Privilege", menuNames);
            }

            for (int i = 0; i < dataLoginUser.size(); i++) {
                dataLoginUser.get(i).setUser_password("null");
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Login Success !!");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<String> changeUsersPassword(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        List<Users> dataLoginUser;
        List<Map> user_role = new ArrayList<>();

        try {
            JSONObject jsonInput = new JSONObject(input);
            dataLoginUser = usersRepository.loginUser(jsonInput.optString("user_name"), jsonInput.optString("user_email"), jsonInput.optString("user_password"));
            if (dataLoginUser.size() == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Wrong current password");
                return response;
            }
            usersRepository.changeUsersPassword(jsonInput.optInt("user_id"), jsonInput.optString("new_user_password"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Password Changed !!");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }


//    //USER-ROLE SECTION
//    public BaseResponse<String> addNewUserRole(String input) throws Exception, SQLException {
//        BaseResponse response = new BaseResponse<>();
//        try {
//            JSONObject jsonInput = new JSONObject(input);
//            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
//            //Token Auth
//            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
//                response.setStatus("500");
//                response.setSuccess(false);
//                response.setMessage("Token Authentication Failed");
//                return response;
//            }
//            String userOnProcess = auth.get("user_name").toString();
//
//            userRoleRepository.save(jsonInput.optInt("user_id"), jsonInput.optInt("role_id"), userOnProcess);
//            response.setStatus("200");
//            response.setSuccess(true);
//            response.setMessage("User Role successfully created");
//
//        } catch (Exception e) {
//            response.setStatus("500");
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//        }
//
//        return response;
//    }
//
//    public BaseResponse<List<Map<String, Object>>> getUserRole(String input) throws Exception, SQLException {
//        BaseResponse response = new BaseResponse<>();
//        List<Map<String, Object>> result = new ArrayList<>();
//        JSONObject jsonInput;
//        String created_date = "%%";
//        String updated_date = "%%";
//        String user_id;
//        String role_id;
//        String status;
//        String created_by;
//        String updated_by;
//        try {
//            jsonInput = new JSONObject(input);
//            if (jsonInput.optString("created_date").length() > 0) {
//                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
//            }
//            if (jsonInput.optString("updated_date").length() > 0) {
//                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
//            }
//            user_id = jsonInput.optInt("user_id") + "";
//            if (user_id.isEmpty() || user_id.compareToIgnoreCase("null") == 0 || user_id.compareToIgnoreCase("0") == 0) {
//                user_id = "%%";
//            }
//            role_id = jsonInput.optInt("role_id") + "";
//            if (role_id.isEmpty() || role_id.compareToIgnoreCase("null") == 0 || role_id.compareToIgnoreCase("0") == 0) {
//                role_id = "%%";
//            }
//            status = jsonInput.optString("status");
//            if (status.isEmpty()) {
//                status = "%%";
//            }
//            created_by = "%" + jsonInput.optString("created_by") + "%";
//            updated_by = "%" + jsonInput.optString("updated_by") + "%";
//            List<UserRole> userRoleList = userRoleRepository.getUserRoleList(user_id, role_id, status, created_by,
//                    created_date, updated_by, updated_date);
//            for (int i = 0; i < userRoleList.size(); i++) {
//                Map resultMap = new HashMap();
//                List<Role> roles = roleRepository.getRoleById(userRoleList.get(i).getRole_id());
//                resultMap.put("role", roles.get(0));
//                resultMap.put("user_role", userRoleList.get(i));
//
//                result.add(resultMap);
//            }
//
//            response.setData(result);
//            response.setStatus("200");
//            response.setSuccess(true);
//            response.setMessage("User Role Listed");
//        } catch (Exception e) {
//            response.setStatus("500");
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//        }
//        return response;
//    }
//
//    public BaseResponse<UserRole> updateUserRole(String input) throws Exception, SQLException {
//        BaseResponse response = new BaseResponse();
//
//        try {
//            JSONObject jsonInput = new JSONObject(input);
//            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
//
//            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
//                response.setStatus("500");
//                response.setSuccess(false);
//                response.setMessage("Token Authentication Failed");
//                return response;
//            }
//            String userOnProcess = auth.get("user_name").toString();
//            userRoleRepository.updateUserRole(jsonInput.optInt("role_id"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("user_role_id"));
//            response.setStatus("200");
//            response.setSuccess(true);
//            response.setMessage("User Role successfully Updated");
//
//
//        } catch (Exception e) {
//            response.setStatus("500");
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//        }
//
//
//        return response;
//    }
//
//    public BaseResponse<UserRole> deleteUserRole(String input) throws Exception, SQLException {
//        BaseResponse response = new BaseResponse();
//
//        try {
//            JSONObject jsonInput = new JSONObject(input);
//            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
//
//            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
//                response.setStatus("500");
//                response.setSuccess(false);
//                response.setMessage("Token Authentication Failed");
//                return response;
//            }
//            String userOnProcess = auth.get("user_name").toString();
//            userRoleRepository.deleteUserRole(jsonInput.optInt("user_role_id"), userOnProcess);
//            response.setStatus("200");
//            response.setSuccess(true);
//            response.setMessage("User-Role successfully deleted");
//        } catch (Exception e) {
//            response.setStatus("500");
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
//        }
//        return response;
//    }

//    public List<Integer> getUserRoleByUserId(int user_id) {
//        List<UserRole> userRoleList = userRoleRepository.getUserRoleByUserId(user_id);
//        List<Integer> roleIdList = new ArrayList<>();
//        for (UserRole role : userRoleList) {
//            roleIdList.add(role.getRole_id());
//        }
//        return roleIdList;
//    }

    //COMPANY SECTION
    public BaseResponse<String> addNewCompany(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        String company_name;
        String company_address;
        String company_phone;
        String company_email;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            company_name = jsonInput.optString("company_name");
            company_address = jsonInput.optString("company_address");
            company_phone = jsonInput.optString("company_phone");
            company_email = jsonInput.optString("company_email");
            //company name  check
            if (company_name.isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Company name can't be empty");
                return response;
            }


            List<Company> companyNameCheckResult = companyRepository.getCompanyByName(company_name);
            if (companyNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Company name already exist / used");
                return response;
            }

            //Save company object
            Company companies = new Company();
            companies.setCompany_name(company_name);
            companies.setCompany_address(company_address);
            companies.setCompany_phone(company_phone);
            companies.setCompany_email(company_email);
            companyRepository.save(companies);


            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Company successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Company>> getCompanyList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String company_name;
        String company_id;
        String company_address;
        String company_email;
        String company_phone;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            company_name = "%" + jsonInput.optString("company_name") + "%";
            company_address = "%" + jsonInput.optString("company_address") + "%";
            company_email = "%" + jsonInput.optString("company_email") + "%";
            company_phone = "%" + jsonInput.optString("company_phone") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Company> getCompanyResult = companyRepository.getCompanyList(company_name, company_id, company_address, company_phone, company_email,
                    status, created_by, created_date, updated_by, updated_date);
            response.setData(getCompanyResult);

            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Company Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Company> updateCompany(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            //company name  check
            List<Company> companyNameCheckResult = companyRepository.getCompanyByNameExceptId(jsonInput.optString("company_name"), jsonInput.optInt("company_id"));
            if (companyNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Company name already exist / used");
                return response;
            }
            companyRepository.updateCompany(jsonInput.optString("company_name"), jsonInput.optString("company_address"), jsonInput.optString("company_phone"),
                    jsonInput.optString("company_email"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("company_id"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Company successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Company> deleteCompany(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            //Check region of company
            List<Region> usedCompanyOnRegion = regionRepository.getRegionByCompanyId(jsonInput.optInt("company_id"));
            if (usedCompanyOnRegion.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("The company still has " + usedCompanyOnRegion.size() + " region(s)");
                return response;
            }

            companyRepository.deleteCompany(jsonInput.optInt("company_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("User successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Company> getCompanyById(int company_id) {
        List<Company> getCompanyResult = new ArrayList<>();
        getCompanyResult = companyRepository.getCompanyById(company_id);
        return getCompanyResult;
    }

    public List<Company> getCompanyByName(String company_name) {
        List<Company> getCompanyResult = new ArrayList<>();
        getCompanyResult = companyRepository.getCompanyByName(company_name);
        return getCompanyResult;
    }

    //REGION SECTION
    public BaseResponse<String> addNewRegion(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        String region_name;
        int company_id;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            region_name = jsonInput.optString("region_name");
            company_id = jsonInput.optInt("company_id");

            //Region name  check
            if (jsonInput.optString("region_name").isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Region name can't be empty");
                return response;
            }
            if (jsonInput.optInt("company_id") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Company can't be empty");
                return response;
            }
            List<Region> regionNameCheckResult = regionRepository.getRegionByName(region_name, company_id);
            if (regionNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Region name already exist / used");
                return response;
            }
            //Save region object
            Region regions = new Region();
            regions.setRegion_name(region_name);
            regions.setCompany_id(company_id);
            regions.setStatus("active");
            regions.setCreated_by(userOnProcess);
            regions.setCreated_date(new Date());
            regions.setUpdated_by("");
            regions.setUpdated_date(new Date());

            regionRepository.save(regions);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Region successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage("Failed create Region : " + e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getRegionList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String region_name;
        String company_id;
        String region_id;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            region_name = "%" + jsonInput.optString("region_name") + "%";
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Region> getRegionResult = regionRepository.getRegionList(region_name, company_id, region_id, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getRegionResult.size(); i++) {
                Map resultMap = new HashMap();
                List<Company> company = getCompanyById(getRegionResult.get(i).getCompany_id());
                resultMap.put("region", getRegionResult.get(i));
                resultMap.put("company_name", company.get(0).getCompany_name());
                resultMap.put("company_id", company.get(0).getCompany_id());

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Region Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Region> updateRegion(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            //Region name  check
            List<Region> regionNameCheckResult = regionRepository.getRegionByNameExceptId(jsonInput.optString("region_name"), jsonInput.optInt("company_id"), jsonInput.optInt("region_id"));
            if (regionNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Region name already exist / used");
                return response;
            }
            if (jsonInput.optString("status").isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Status must be filled, can't be empty");
                return response;
            }
            regionRepository.updateRegion(jsonInput.optString("region_name"), jsonInput.optInt("company_id"), jsonInput.optString("status"),
                    userOnProcess, jsonInput.optInt("region_id"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Region successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Region> deleteRegion(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            //Check branch of region
            List<Branch> usedRegionOnBranch = branchRepository.getBranchByRegionId(jsonInput.optInt("region_id"));
            if (usedRegionOnBranch.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("The Region still has " + usedRegionOnBranch.size() + " branch(es)");
                return response;
            }
            regionRepository.deleteRegion(jsonInput.optInt("region_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Region successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Region> getRegionById(int region_id) {
        List<Region> getCompanyResult = new ArrayList<>();
        getCompanyResult = regionRepository.getRegionById(region_id);
        return getCompanyResult;
    }

//    public List<Region> getRegionByName(String region_name) {
//        List<Region> getCompanyResult = new ArrayList<>();
//        getCompanyResult = regionRepository.getRegionByName(region_name);
//        return getCompanyResult;
//    }

    //BRANCH SECTION
    public BaseResponse<String> addNewBranch(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        String branch_name;
        int region_id;
        int company_id;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            branch_name = jsonInput.optString("branch_name");
            region_id = jsonInput.optInt("region_id");
            company_id = jsonInput.optInt("company_id");

            //Branch name  check
            if (branch_name.isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Branch name can't be empty");
                return response;
            }
            if (jsonInput.optString("region_id").compareToIgnoreCase("all") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Can't select all, please select specific region");
                return response;
            }
            if (region_id == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Region can't be empty");
                return response;
            }

            List<Branch> branchNameCheckResult = branchRepository.getBranchByName(branch_name, region_id, company_id);
            if (branchNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Branch name already exist / used");
                return response;
            }
            Branch branches = new Branch();
            branches.setBranch_name(branch_name);
            branches.setRegion_id(region_id);
            branches.setCompany_id(company_id);
            branches.setStatus("active");
            branches.setCreated_by(userOnProcess);
            branches.setCreated_date(new Date());
            branches.setUpdated_by("");
            branches.setUpdated_date(new Date());

            branchRepository.save(branches);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Branch successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getBranchList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String branch_name;
        String company_id;
        String region_id;
        String branch_id;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            branch_name = "%" + jsonInput.optString("branch_name") + "%";
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Branch> getBranchResult = branchRepository.getBranchList(branch_name, region_id, branch_id, company_id, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getBranchResult.size(); i++) {
                Map resultMap = new HashMap();
                List<Region> region = getRegionById(getBranchResult.get(i).getRegion_id());
                List<Company> company = getCompanyById(region.get(0).getCompany_id());
                resultMap.put("company_name", company.get(0).getCompany_name());
                resultMap.put("company_id", company.get(0).getCompany_id());
                resultMap.put("branch", getBranchResult.get(i));
                resultMap.put("region_name", region.get(0).getRegion_name());
                resultMap.put("region_id", region.get(0).getRegion_id());

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Branch Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Branch> updateBranch(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            //Branch name  check
            List<Branch> branchNameCheckResult = branchRepository.getBranchByNameExceptId(jsonInput.optString("branch_name"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optInt("branch_id"));
            if (branchNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Branch name already exist / used");
                return response;
            }

            branchRepository.updateBranch(jsonInput.optString("branch_name"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"),
                    jsonInput.optString("status"), userOnProcess, jsonInput.optInt("branch_id"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Branch successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Branch> deleteBranch(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            //Check promo of branch
            List<Promo> usedBranchOnPromo = promoRepository.getPromoByBranchId(jsonInput.optInt("branch_id"));
            if (usedBranchOnPromo.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("The Branch still has " + usedBranchOnPromo.size() + " promo(s)");
                return response;
            }

            //Check playlist of branch
            List<Playlist> usedBranchOnPlaylist = playlistRepository.getPlaylistByBranchId(jsonInput.optInt("branch_id"));
            if (usedBranchOnPlaylist.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("The Branch still has " + usedBranchOnPlaylist.size() + " playlist(s)");
                return response;
            }


            branchRepository.deleteBranch(jsonInput.optInt("branch_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Branch successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Branch> getBranchById(int branch_id) {
        List<Branch> getBranchResult = new ArrayList<>();
        getBranchResult = branchRepository.getBranchById(branch_id);
        return getBranchResult;
    }

//    public List<Branch> getBranchByName(String branch_name) {
//        List<Branch> getBranchResult = new ArrayList<>();
//        getBranchResult = branchRepository.getBranchByName(branch_name);
//        return getBranchResult;
//    }

    //PROMO SECTION
    public BaseResponse<String> addNewPromo(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            //Promo tittle  check
            List<Promo> promoTittleCheckResult = promoRepository.getPromoByTittle(jsonInput.optString("tittle"), jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"));
            if (promoTittleCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Promo Tittle already exist / used");
                return response;
            }

            String file = "";
            String thumbnail = "";
            if (!jsonInput.optString("file").isEmpty() && !jsonInput.optString("file_name").isEmpty()) {
                Map<String, String> fileAddResult = addFile(jsonInput.optString("file_name"), jsonInput.optString("file"), "promo").getData();
                file = fileAddResult.get("file");
                thumbnail = fileAddResult.get("thumbnail");
            }
            if (!jsonInput.optString("url_resource").isEmpty()) {
                thumbnail = "thumbnail_url.png";
            }

            if (jsonInput.optInt("company_id") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Unknown company, please choose existing company.");
                return response;
            }
            promoRepository.save(jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optString("tittle"), file, jsonInput.optString("description"),
                    jsonInput.optString("popup"), jsonInput.optString("popup_description"), jsonInput.optString("start_date"), jsonInput.optString("end_date"), userOnProcess, thumbnail);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Promo successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage("Failed create branch : " + e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getPromoList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String branch_id;
        String region_id;
        String company_id;
        String tittle;
        String file;
        String description;
        String popup;
        String popup_description;
        String start_date;
        String end_date;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.isEmpty() || region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.isEmpty() || company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            tittle = "%" + jsonInput.optString("tittle") + "%";
            file = "%" + jsonInput.optString("file") + "%";
            description = "%" + jsonInput.optString("description") + "%";
            popup = "%" + jsonInput.optString("popup") + "%";
            popup_description = "%" + jsonInput.optString("popup_description") + "%";
            start_date = "%" + jsonInput.optString("start_date") + "%";
            end_date = "%" + jsonInput.optString("end_date") + "%";

            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Promo> getPromoResult = promoRepository.getPromoList(branch_id, region_id, company_id, tittle, file, description, popup, popup_description,
                    start_date, end_date, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getPromoResult.size(); i++) {
                Map resultMap = new HashMap();
                if (getPromoResult.get(i).getBranch_id() != 0) {
                    List<Branch> branch = branchRepository.getBranchById(getPromoResult.get(i).getBranch_id());
                    resultMap.put("branch", branch.get(0));
                } else {
                    Branch branch = new Branch();
                    branch.setBranch_id(0);
                    branch.setBranch_name("All Branches");
                    resultMap.put("branch", branch);
                }
                if (getPromoResult.get(i).getRegion_id() != 0) {
                    List<Region> regions = regionRepository.getRegionById(getPromoResult.get(i).getRegion_id());
                    resultMap.put("region", regions.get(0));
                } else {
                    Region region = new Region();
                    region.setRegion_id(0);
                    region.setRegion_name("All Regions");
                    resultMap.put("region", region);
                }
                if (getPromoResult.get(i).getCompany_id() != 0) {
                    List<Company> companies = companyRepository.getCompanyById(getPromoResult.get(i).getCompany_id());
                    resultMap.put("company", companies.get(0));
                } else {
                    resultMap.put("company", "All Companies");
                }

                resultMap.put("promo", getPromoResult.get(i));

                result.add(resultMap);
            }
            for (Map map : result) {
                Promo promo = (Promo) map.get("promo");
                if (!promo.getThumbnail().isEmpty()) {
                    logger.info("promo thumbnail : " + promo.getThumbnail());
                    try {
                        promo.setThumbnail(getFile(promo.getThumbnail(), "promo").getData().get("file_base64").toString());
                    } catch (Exception e) {
                        logger.info("promo error : " + e.getMessage());
                        promo.setThumbnail("");
                    }
                }
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Promo Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Promo> updatePromo(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            //Promo tittle  check
            List<Promo> promoTittleCheckResult = promoRepository.getPromoByTittleExceptId(jsonInput.optString("tittle"), jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optInt("promo_id"));
            if (promoTittleCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Promo Tittle already exist / used");
                return response;
            }
            String file = jsonInput.optString("file");
            String thumbnail = getPromoById(jsonInput.optInt("promo_id")).get(0).getThumbnail();
            if (!jsonInput.optString("file").isEmpty() && !jsonInput.optString("file_name").isEmpty()) {
                Map<String, String> imageAddResult = addFile(jsonInput.optString("file_name"), jsonInput.optString("file"), "promo").getData();
                file = imageAddResult.get("file");
                thumbnail = imageAddResult.get("thumbnail");

            }
            if (jsonInput.optInt("company_id") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Unknown company, please choose existing company.");
                return response;
            }
            promoRepository.updatePromo(jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optString("tittle"), file, jsonInput.optString("description"),
                    jsonInput.optString("popup"), jsonInput.optString("popup_description"), jsonInput.optString("start_date"), jsonInput.optString("end_date"),
                    jsonInput.optString("status"), userOnProcess, jsonInput.optInt("promo_id"), thumbnail);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Promo successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Promo> deletePromo(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            promoRepository.deletePromo(jsonInput.optInt("promo_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Promo successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Promo> getPromoById(int promo_id) {
        List<Promo> getPromoResult = new ArrayList<>();
        getPromoResult = promoRepository.getPromoById(promo_id);
        return getPromoResult;
    }


    //DEVICE SECTION
    public BaseResponse<String> addNewDevice(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        int company_id;
        int region_id;
        int branch_id;
        String device_name;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            company_id = jsonInput.getInt("company_id");
            region_id = jsonInput.getInt("region_id");
            branch_id = jsonInput.getInt("branch_id");
            device_name = jsonInput.optString("device_name");

            //Device name  check
            List<Device> deviceNameCheckResult = deviceRepository.getDeviceByName(device_name);
            if (deviceNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Device name already exist / used");
                return response;
            }
            if (jsonInput.optString("device_name").isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Device name can't be empty");
                return response;
            }

            List<Company> companyList = getCompanyById(company_id);
            if (companyList.size() == 0) {
                return notFoundComponent("Company");
            }
            Device device = new Device();
            device.setDevice_name(device_name);
            device.setCompany_id(company_id);
            device.setRegion_id(region_id);
            device.setBranch_id(branch_id);
            device.setLicense_key("");
            device.setDevice_unique_id("");
            device.setStatus("active");
            device.setCreated_by(userOnProcess);
            device.setCreated_date(new Date());
            device.setUpdated_by("");
            device.setUpdated_date(new Date());


            deviceRepository.save(device);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Device successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Device>> getDeviceList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String device_name;
        String status;
        String created_by;
        String updated_by;
        String branch_id;
        String region_id;
        String company_id;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.isEmpty() || region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.isEmpty() || company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            device_name = "%" + jsonInput.optString("device_name") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Device> getDeviceResult = deviceRepository.getDeviceList(branch_id, region_id, company_id, device_name, status, created_by, created_date, updated_by, updated_date);
            for (Device device : getDeviceResult) {
                device.setLicense_key(cmsEncryptDecrypt.decrypt(device.getLicense_key()));
            }

            response.setData(getDeviceResult);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Device Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Device> updateDevice(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        String device_name;
        String status;
        int device_id;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            device_name = jsonInput.optString("device_name");
            status = jsonInput.optString("status");
            device_id = jsonInput.optInt("device_id");
            //Device name  check
            List<Device> deviceNameCheckResult = deviceRepository.getDeviceByNameExceptId(device_name, device_id);
            if (deviceNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Device name already exist / used");
                return response;
            }
            if (jsonInput.optString("device_name").isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Can't update device name to empty");
                return response;
            }
            if (jsonInput.optString("status").isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Status must be filled, can't be empty");
                return response;
            }
            deviceRepository.updateDevice(device_name, status, userOnProcess, device_id);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Device successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Device> deleteDevice(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();


            deviceRepository.deleteDevice(jsonInput.optInt("device_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Device successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    public BaseResponse checkDeviceUniqueId(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            String device_unique_id = jsonInput.getString("device_unique_id");
            List<Device> checkedDeviceList = deviceRepository.checkDeviceUniqueId(device_unique_id);
            if (checkedDeviceList.size() > 0) {
                response.setData(checkedDeviceList.get(0).getDevice_id());
                response.setStatus("200");
                response.setSuccess(true);
                response.setMessage("Device already registered");
            } else {
                response.setStatus("404");
                response.setSuccess(false);
                response.setMessage("device is not registered yet, please enter security code");
            }

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse authLicenseKeyAndDeviceUniqueId(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            String license_key = cmsEncryptDecrypt.encrypt(jsonInput.getString("license_key").getBytes());
            String device_unique_id = jsonInput.getString("device_unique_id");

            List<Device> checkValidCountLicense = deviceRepository.checkLicenseKeyCount(license_key);
            if (checkValidCountLicense.size() > 1) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Cannot use the license, multiple device found with this license");
                return response;
            }

            List<Device> checkedLicenseKey = deviceRepository.checkLicenseKeyUsed(license_key);
            if (checkedLicenseKey.size() > 0) {
                deviceRepository.updateLicenseKeyDeviceUniqueIdPair(checkedLicenseKey.get(0).getDevice_id(), device_unique_id);
            } else {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Wrong license key or license key already used");
                return response;
            }
            response.setData(checkedLicenseKey.get(0).getDevice_id());
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Device successfully registered");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage("Failed to check license key :" + e.getMessage());
        }
        return response;
    }

    public List<Device> getDeviceById(int device_id) {
        List<Device> getDeviceResult = new ArrayList<>();
        getDeviceResult = deviceRepository.getDeviceById(device_id);
        return getDeviceResult;
    }

    public List<Device> getDeviceByName(String device_name) {
        List<Device> getDeviceResult = new ArrayList<>();
        getDeviceResult = deviceRepository.getDeviceByName(device_name);
        return getDeviceResult;
    }

    //POSITION SECTION
    public BaseResponse<String> addNewPosition(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        int profile_id;
        String box;
        String x_pos;
        String y_pos;
        String width;
        String height;
        String measurement;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            profile_id = jsonInput.optInt("profile_id");
            if (profile_id == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Please select exisitng profile");
                return response;
            }
            box = jsonInput.optString("box");
            x_pos = jsonInput.optString("x_pos");
            y_pos = jsonInput.optString("y_pos");
            width = jsonInput.optString("width");
            height = jsonInput.optString("height");
            measurement = jsonInput.optString("measurement");
            Position position = new Position();
            position.setProfile_id(profile_id);
            position.setBox(box);
            position.setX_pos(x_pos);
            position.setY_pos(y_pos);
            position.setWidth(width);
            position.setHeight(height);
            position.setStatus("active");
            position.setMeasurement(measurement);
            position.setCreated_by(userOnProcess);
            position.setCreated_date(new Date());
            position.setUpdated_by("");
            position.setUpdated_date(new Date());

            positionRepository.save(position);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Position successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getPositionList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String profile_id;
        String box;
        String x_pos;
        String y_pos;
        String width;
        String height;
        String measurement;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }

            profile_id = jsonInput.optInt("profile_id") + "";
            if (profile_id.isEmpty() || profile_id.compareToIgnoreCase("null") == 0 || profile_id.compareToIgnoreCase("0") == 0) {
                profile_id = "%%";
            }
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            box = "%" + jsonInput.optString("box") + "%";
            x_pos = "%" + jsonInput.optString("x_pos") + "%";
            y_pos = "%" + jsonInput.optString("y_pos") + "%";
            width = "%" + jsonInput.optString("width") + "%";
            height = "%" + jsonInput.optString("height") + "%";
            measurement = "%" + jsonInput.optString("measurement") + "%";

            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Position> getPositionResult = positionRepository.getPositionList(profile_id, box, x_pos, y_pos, width, height, measurement, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getPositionResult.size(); i++) {
                Map resultMap = new HashMap();
                List<Profile> profiles = profileRepository.getProfileById(getPositionResult.get(i).getProfile_id());
                resultMap.put("position", getPositionResult.get(i));
                resultMap.put("profile_name", profiles.get(0).getProfile_name());
                resultMap.put("profile_id", profiles.get(0).getProfile_id());

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Position Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Position> updatePosition(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        String box;
        String x_pos;
        String y_pos;
        String width;
        String height;
        String measurement;
        String status;
        int position_id;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            box = jsonInput.optString("box");
            x_pos = jsonInput.optString("x_pos");
            y_pos = jsonInput.optString("y_pos");
            width = jsonInput.optString("width");
            height = jsonInput.optString("height");
            measurement = jsonInput.optString("measurement");
            status = jsonInput.optString("status");
            position_id = jsonInput.getInt("position_id");

            List<Position> positionList = getPositionById(position_id);
            if (positionList.size() == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Cant' find position with id :" + position_id);
                return response;
            }

            positionRepository.updatePosition(box, x_pos, y_pos, width, height, status, measurement, userOnProcess, position_id);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Position successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Position> deletePosition(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        int position_id;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            position_id = jsonInput.optInt("position_id");
            List<Position> positions = positionRepository.getPositionById(position_id);

            //Check playlist use this position
            List<Profile> usedPositionOnProfile = profileRepository.getProfileById(positions.get(0).getProfile_id());
            if (usedPositionOnProfile.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Position still used on  active profile(s)");
                return response;
            }
            List<Position> positionList = getPositionById(position_id);
            if (positionList.size() == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Cant' find position with id :" + position_id);
                return response;
            }

            positionRepository.deletePosition(jsonInput.optInt("position_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Position successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Position> getPositionById(int position_id) {
        List<Position> getPositionResult = new ArrayList<>();
        getPositionResult = positionRepository.getPositionById(position_id);
        return getPositionResult;
    }


    //RESOURCE SECTION
    public BaseResponse<String> addNewResource(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        String resource_name;
        String type;
        String file_name;
        String url_resource;
        int duration;
        String stretch;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            resource_name = jsonInput.optString("resource_name");
            type = jsonInput.optString("type");
            file_name = jsonInput.optString("file_name");
            url_resource = jsonInput.optString("url_resource");
            duration = jsonInput.optInt("duration");
            stretch = jsonInput.optString("stretch");


            if (resource_name.isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Resource name can't be empty");
                return response;
            }

            //Resource name  check
            List<Resource> resourceNameCheckResult = resourceRepository.getResourceByName(resource_name);
            if (resourceNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Resource name already exist / used");
                return response;
            }

            if (type.compareToIgnoreCase("application/vnd.openxmlformats-officedocument.presentationml.presentation") == 0 || type.compareToIgnoreCase("application/vnd.ms-powerpoint") == 0) {
                String fileExtension = file_name.split("\\.")[1];
                if (fileExtension.compareToIgnoreCase("pptx") != 0) {
                    response.setStatus("500");
                    response.setSuccess(false);
                    response.setMessage("Power point files only accept pptx format");
                    return response;
                }
            }

            String file = "";
            String thumbnail = "";
            if (!jsonInput.optString("file").isEmpty() && !file_name.isEmpty()) {
                Map<String, String> fileAddResult = addFile(file_name, jsonInput.optString("file"), "resource").getData();
                ParseToImage(file_name);
                file = fileAddResult.get("file");
                thumbnail = fileAddResult.get("thumbnail");
            }

//            //PPT HANDLER FOR ANDROID
//            if (jsonInput.optString("type").compareToIgnoreCase("application/vnd.openxmlformats-officedocument.presentationml.presentation") == 0 || jsonInput.optString("type").compareToIgnoreCase("application/vnd.ms-powerpoint") == 0) {
//                String fileExtension = jsonInput.optString("file_name").split("\\.")[1];
//                String finalFileName = jsonInput.optString("file_name");
//                if (fileExtension.compareToIgnoreCase("pptx") != 0) {
//                    finalFileName.replace(fileExtension, "pptx");
//                    duplicateFile(finalFileName, jsonInput.optString("file"));
//                }
//
//            }

            if (!url_resource.isEmpty()) {
                thumbnail = "thumbnail_url.png";
            }
            Resource resources = new Resource();
            resources.setResource_name(resource_name);
            resources.setType(type);
            resources.setThumbnail(thumbnail);
            resources.setFile(file);
            resources.setDuration(duration);
            resources.setStretch(stretch);
            resources.setStatus("active");
            resources.setCreated_by(userOnProcess);
            resources.setCreated_date(new Date());
            resources.setUpdated_by("");
            resources.setUpdated_date(new Date());
            resources.setUrl_resource(url_resource);


            resourceRepository.save(resources);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Resource successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse getResourceList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        Map<String, Object> result = new HashMap<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String resource_name;
        String type;
        String thumbnail;
        String file;
        String duration;
        String stretch;
        String status;
        String created_by;
        String updated_by;
        int limit = 0;
        int offset = 0;
        int startingData = 0;
        List<Resource> resourceListPaged = new ArrayList<>();
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            resource_name = "%" + jsonInput.optString("resource_name") + "%";
            type = "%" + jsonInput.optString("type") + "%";
            thumbnail = "%" + jsonInput.optString("thumbnail") + "%";
            file = "%" + jsonInput.optString("file") + "%";
            duration = jsonInput.optInt("duration") + "";
            if (duration.isEmpty() || duration.compareToIgnoreCase("null") == 0 || duration.compareToIgnoreCase("0") == 0) {
                duration = "%%";
            }
            stretch = "%" + jsonInput.optString("stretch") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            limit = jsonInput.getInt("limit");
            offset = jsonInput.getInt("offset");
            startingData = (offset - 1) * limit;
            List<Resource> getResourceResult = resourceRepository.getResourceList(resource_name, type, thumbnail, file, duration,
                    stretch, status, created_by, created_date, updated_by, updated_date);
            int maxPage = (int) Math.ceil(getResourceResult.size() / (limit * 1.0));
            if (getResourceResult.size() > 0) {
                resourceListPaged = getResourceResult.subList(startingData, Math.min((startingData + limit), getResourceResult.size()));
            }

            for (Resource resource : resourceListPaged) {
                if (!resource.getThumbnail().isEmpty()) {
                    try {
                        resource.setThumbnail(getFile(resource.getThumbnail(), "resource").getData().get("file_base64").toString());
                    } catch (Exception e) {
                        resource.setThumbnail("");
                    }
                }
            }
            result.put("resourceData", resourceListPaged);
            result.put("maxPage", maxPage);

            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Resource Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse getResourceListAll(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String resource_name;
        String type;
        String thumbnail;
        String file;
        String duration;
        String stretch;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            resource_name = "%" + jsonInput.optString("resource_name") + "%";
            type = "%" + jsonInput.optString("type") + "%";
            thumbnail = "%" + jsonInput.optString("thumbnail") + "%";
            file = "%" + jsonInput.optString("file") + "%";
            duration = jsonInput.optInt("duration") + "";
            if (duration.isEmpty() || duration.compareToIgnoreCase("null") == 0 || duration.compareToIgnoreCase("0") == 0) {
                duration = "%%";
            }
            stretch = "%" + jsonInput.optString("stretch") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Resource> getResourceResult = resourceRepository.getResourceList(resource_name, type, thumbnail, file, duration,
                    stretch, status, created_by, created_date, updated_by, updated_date);

            for (Resource resource : getResourceResult) {
                resource.setThumbnail("");
            }

            response.setData(getResourceResult);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Resource Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Resource> updateResource(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            //Resource name  check
            List<Resource> resourceNameCheckResult = resourceRepository.getResourceByNameExceptId(jsonInput.optString("resource_name"), jsonInput.optInt("resource_id"));
            if (resourceNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Resource name already exist / used");
                return response;
            }
            String file = jsonInput.optString("file");
            String thumbnail = getResourceById(jsonInput.optInt("resource_id")).get(0).getThumbnail();
            if (!jsonInput.optString("file").isEmpty() && !jsonInput.optString("file_name").isEmpty()) {
                Map<String, String> fileAddResult = addFile(jsonInput.optString("file_name"), jsonInput.optString("file"), "resource").getData();
                file = fileAddResult.get("file");
                thumbnail = fileAddResult.get("thumbnail");
            }
            resourceRepository.updateResource(jsonInput.optString("resource_name"), jsonInput.optString("type"), thumbnail, file,
                    jsonInput.optInt("duration"), jsonInput.optString("stretch"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("resource_id"), jsonInput.optString("url_resource"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Resource successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Resource> deleteResource(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            //Check playlist use this resource
            List<PlaylistResource> usedResourceOnPlaylist = playlistResourceRepository.getPlaylistResourceByResourceId(jsonInput.optInt("resource_id"));
            if (usedResourceOnPlaylist.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Resource still used on " + usedResourceOnPlaylist.size() + " playlist(s)");
                return response;
            }

            resourceRepository.deleteResource(jsonInput.optInt("resource_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Resource successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Resource> getResourceById(int resource_id) {
        List<Resource> getResourceResult = new ArrayList<>();
        getResourceResult = resourceRepository.getResourceById(resource_id);
        return getResourceResult;
    }

    public List<Resource> getResourceByName(String resource_name) {
        List<Resource> getResourceResult = new ArrayList<>();
        getResourceResult = resourceRepository.getResourceByName(resource_name);
        return getResourceResult;
    }

    //PLAYLIST SECTION
    public BaseResponse<String> addNewPlaylist(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        String playlist_name;
        int profile_id;
        int branch_id;
        int region_id;
        int company_id;
        boolean is_default;
        String start_date;
        String end_date;
        JSONArray position_list;
        String user_token;
        try {
            JSONObject jsonInput = new JSONObject(input);
            user_token = jsonInput.optString("user_token");
            Map<String, Object> auth = tokenAuthentication(user_token);
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            playlist_name = jsonInput.optString("playlist_name");
            branch_id = jsonInput.optInt("branch_id");
            region_id = jsonInput.optInt("region_id");
            company_id = jsonInput.optInt("company_id");
            profile_id = jsonInput.optInt("profile_id");
            start_date = jsonInput.optString("start_date");
            end_date = jsonInput.optString("end_date");

            int addNewAvailability = playlistRepository.checkAddNewPlaylistAvailability(profile_id, start_date, end_date);
            if (addNewAvailability == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Can't create playlist : Playlist already exist for that period of time (select another start date /end date)");
                return response;
            }


            position_list = jsonInput.getJSONArray("position_list");


            //Playlist name  check
            List<Playlist> playlistNameCheckResult = playlistRepository.getPlaylistByName(playlist_name, branch_id, region_id, company_id);
            if (playlistNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Can't create playlist : Playlist name already exist / used");
                return response;
            }

            int currentSort = 1;
//            List<Playlist> latestSort = playlistRepository.getSortOrder(position_id, branch_id);
//            if (latestSort.size() > 0) {
//                currentSort = latestSort.get(0).getSort() + 1;
//            }

            String is_defaultStr = jsonInput.optString("is_default");
            if (is_defaultStr.isEmpty() || is_defaultStr.compareToIgnoreCase("null") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Is default is empty, Playlist must define as default or not");
                return response;
            } else {
                is_default = Boolean.valueOf(is_defaultStr);
            }
            Playlist playlist = new Playlist();
            playlist.setCompany_id(company_id);
            playlist.setRegion_id(region_id);
            playlist.setBranch_id(branch_id);
            playlist.setPlaylist_name(playlist_name);
            playlist.setProfile_id(profile_id);
            playlist.setStart_date(new SimpleDateFormat("yyyy-MM-dd HHmm:ss").parse(start_date));
            playlist.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HHmm:ss").parse(end_date));
            playlist.setSort(currentSort);
            playlist.setStatus("active");
            playlist.setIs_default(is_default);
            playlist.setCreated_by(userOnProcess);
            playlist.setCreated_date(new Date());
            playlist.setUpdated_by("");
            playlist.setUpdated_date(new Date());
            playlistRepository.save(playlist);

//            playlistRepository.save(jsonInput.optString("playlist_name"), jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optInt("position_id"),
//                    start_date, end_date, currentSort, is_default, userOnProcess);

            //Inserting resource to playlist_resource

            for (int a = 0; a < position_list.length(); a++) {
                List<Playlist> insertedPlaylist = playlistRepository.getPlaylistByNameInsertedValues(playlist_name);
                JSONObject object = position_list.getJSONObject(a);
                JSONArray playlistResourceItem = object.getJSONArray("resource_list");
                Playlist playlistInserted = insertedPlaylist.get(0);
                int position_id = object.optInt("position_id");
                int resource_id;
                int order;
                int playlist_id = playlistInserted.getPlaylist_id();
                for (int i = 0; i < playlistResourceItem.length(); i++) {
                    JSONObject obj = playlistResourceItem.getJSONObject(i);
                    resource_id = obj.getInt("resource_id");
                    order = obj.getInt("order");
                    addPlaylistResource(user_token, resource_id, playlist_id, order, position_id);
                }

            }
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Playlist successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage("Can't create playlist : " + e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Playlist>> getPlaylistList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String playlist_name;
        String branch_id;
        String region_id;
        String company_id;
        String position_id;
        String is_default;

        String start_date;
        String end_date;
        String sort;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            playlist_name = "%" + jsonInput.optString("playlist_name") + "%";
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.isEmpty() || region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.isEmpty() || company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            position_id = jsonInput.optInt("position_id") + "";
            if (position_id.isEmpty() || position_id.compareToIgnoreCase("null") == 0 || position_id.compareToIgnoreCase("0") == 0) {
                position_id = "%%";
            }

            start_date = "%" + jsonInput.optString("start_date") + "%";
            end_date = "%" + jsonInput.optString("end_date") + "%";
            is_default = jsonInput.optString("is_default") + "";
            if (is_default.isEmpty() || is_default.compareToIgnoreCase("null") == 0) {
                is_default = "%%";
            }
            sort = jsonInput.optInt("sort") + "";
            if (sort.isEmpty() || sort.compareToIgnoreCase("null") == 0 || sort.compareToIgnoreCase("0") == 0) {
                sort = "%%";
            }
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Playlist> getResultPlayList = playlistRepository.getPlaylistList(playlist_name, branch_id, region_id, company_id, position_id, start_date,
                    end_date, sort, status, is_default, created_by, created_date, updated_by, updated_date);
//            logger.info("playlist_name: " + playlist_name + ",branch_id: " + branch_id + ",region_id: " + region_id + ",company_id:" + company_id + ",position_id: " + position_id + ",start_date: " + start_date +
//                    ",end_date: " + end_date + ",sort: " + sort + ",status: " + status + ",is_default: " + is_default + ",created_by: " + created_by + ",created_date: " + created_date + ",updated_by: " + updated_by + ",updated_date: " + updated_date);

            for (int i = 0; i < getResultPlayList.size(); i++) {
                Playlist playlist = getResultPlayList.get(i);
                logger.info("playlist count : " + getResultPlayList);
                Map resultMap = new HashMap();
                if (getResultPlayList.get(i).getBranch_id() != 0) {
                    List<Branch> branch = branchRepository.getBranchById(getResultPlayList.get(i).getBranch_id());
                    resultMap.put("branch", branch.get(0));
                } else {
                    Branch branch = new Branch();
                    branch.setBranch_id(0);
                    branch.setBranch_name("All Branches");
                    resultMap.put("branch", branch);
                }
//                logger.info("branch ok");
                if (getResultPlayList.get(i).getRegion_id() != 0) {
                    List<Region> regions = regionRepository.getRegionById(getResultPlayList.get(i).getRegion_id());
                    resultMap.put("region", regions.get(0));
                } else {
                    Region region = new Region();
                    region.setRegion_id(0);
                    region.setRegion_name("All Regions");
                    resultMap.put("region", region);
                }
//                logger.info("region ok");
                if (getResultPlayList.get(i).getCompany_id() != 0) {
                    List<Company> companies = companyRepository.getCompanyById(getResultPlayList.get(i).getCompany_id());
                    resultMap.put("company", companies.get(0));
                } else {
                    resultMap.put("company", "All Companies");
                }
//                logger.info("company ok");
                List<Profile> profileList = profileRepository.getProfileById(playlist.getProfile_id());
//                List<Position> positions = positionRepository.getPositionById(getResultPlayList.get(i).getPosition_id());
//                logger.info("position ok");
//                List<Device> devices = deviceRepository.getDeviceById(positions.get(0).getProfile_id());
//                logger.info("device ok");


//                resultMap.put("device", devices.get(0));
                resultMap.put("profile", profileList.get(0));
                resultMap.put("playlist", getResultPlayList.get(i));

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Playlist Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Playlist> updatePlaylist(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        String playlist_name;
        String start_date;
        String end_date;
        String status;
        int playlist_id;
        int profile_id;

        int branch_id;
        int region_id;
        int company_id;
        boolean is_default;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            playlist_name = jsonInput.optString("playlist_name");
            start_date = jsonInput.optString("start_date");
            end_date = jsonInput.optString("end_date");
            status = jsonInput.optString("status");
            playlist_id = jsonInput.optInt("playlist_id");
            branch_id = jsonInput.optInt("branch_id");
            region_id = jsonInput.optInt("region_id");
            company_id = jsonInput.optInt("company_id");


            List<Profile> profiles = profileRepository.getProfileByPlaylistId(playlist_id);
            if (profiles.size() != 0) {
                profile_id = profiles.get(0).getProfile_id();
            } else {
                response.setStatus("404");
                response.setSuccess(false);
                response.setMessage("Can't update playlist : profile for this playlist not found");
                return response;
            }

            int addNewAvailability = playlistRepository.checkAddNewPlaylistAvailability(profile_id, start_date, end_date);
            if (addNewAvailability == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Can't update playlist : Playlist already exist for that period of time (select another start date /end date)");
                return response;
            }


            //Playlist name  check
            List<Playlist> playlistNameCheckResult = playlistRepository.getPlaylistByNameExceptId(playlist_name, branch_id, region_id, company_id, playlist_id);
            if (playlistNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Can't update playlist  : Playlist name already exist / used");
                return response;
            }


            String is_defaultStr = jsonInput.optString("is_default");
            if (is_defaultStr.isEmpty() || is_defaultStr.compareToIgnoreCase("null") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Is default is empty, Playlist must define as default or not");
                return response;
            } else {
                is_default = Boolean.valueOf(is_defaultStr);
            }

            playlistRepository.updatePlaylist(playlist_name, start_date, end_date, status, is_default,
                    userOnProcess, playlist_id);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Playlist successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Playlist> deletePlaylist(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            playlistRepository.deletePlaylist(jsonInput.optInt("playlist_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Playlist successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Playlist> getPlaylistById(int playlist_id) {
        List<Playlist> getPlaylistResource = new ArrayList<>();
        getPlaylistResource = playlistRepository.getPlaylistById(playlist_id);
        return getPlaylistResource;
    }

    public BaseResponse getPlaylistByProfileId(String input) throws SQLException, Exception {
        BaseResponse response = new BaseResponse();
        List results = new ArrayList();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            int profile_id = jsonInput.getInt("profile_id");
            List<Position> getPositionByProfileId = positionRepository.getPositionByProfileIdBasedOnPlaylist(profile_id);
            for (int i = 0; i < getPositionByProfileId.size(); i++) {
                Map resultData = new HashMap();
                Position position = getPositionByProfileId.get(i);
                List<Playlist> playlistList = playlistRepository.getPlaylistByProfileId(position.getProfile_id());
                resultData.put("position_id", position.getPosition_id());
                resultData.put("box", position.getBox());
                resultData.put("x_position", position.getX_pos());
                resultData.put("y_position", position.getY_pos());
                resultData.put("height", position.getHeight());
                resultData.put("weight", position.getWidth());
                resultData.put("uom", position.getMeasurement());
                List playlistResult = new ArrayList();
                for (int j = 0; j < playlistList.size(); j++) {
                    Map resultPlayListMap = new HashMap();
                    Playlist playlist = playlistList.get(j);
                    List<PlaylistResource> getPlaylistResource = playlistResourceRepository.getPlaylistResourceByPlaylistAndPosition(playlist.getPlaylist_id(), position.getPosition_id());
                    resultPlayListMap.put("playlist_id", playlist.getPlaylist_id());
                    resultPlayListMap.put("playlist_name", playlist.getPlaylist_name());
                    resultPlayListMap.put("start_date", playlist.getStart_date());
                    resultPlayListMap.put("end_date", playlist.getEnd_date());
                    resultPlayListMap.put("is_default", playlist.isIs_default());
                    List resourcesResult = new ArrayList();
                    for (int k = 0; k < getPlaylistResource.size(); k++) {
                        Map resultResourceMap = new HashMap();
                        PlaylistResource playlistResource = getPlaylistResource.get(k);
                        List<Resource> resourceList = resourceRepository.getResourceById(playlistResource.getResource_id());
                        Resource resource = resourceList.get(0);
                        resultResourceMap.put("order", playlistResource.getOrder());
                        resultResourceMap.put("resource_id", resource.getResource_id());
                        resultResourceMap.put("resource_name", resource.getResource_name());
                        resultResourceMap.put("resource_type", resource.getType());
                        resultResourceMap.put("resource_duration", resource.getDuration());
                        resultResourceMap.put("resource_stretch", resource.getStretch());
                        resultResourceMap.put("resource_url", resource.getUrl_resource());
                        List<String> urlDownload = new ArrayList<>();
                        if (!resource.getType().isEmpty()) {
                            String type = resource.getType();
//                            logger.info("resource type : " + type);
                            String fileExtension = resource.getFile().split("\\.")[1];
//                            logger.info("file extension : " + fileExtension);
                            String fileName = resource.getFile();
                            if (type.compareToIgnoreCase("application/vnd.openxmlformats-officedocument.presentationml.presentation") == 0 || type.compareToIgnoreCase("application/vnd.ms-powerpoint") == 0) {
//                                logger.info("its power point");
                                List<String> pptImageList = getPresentationImage(fileName.split("\\.")[0]);
//                                logger.info("image generated : " + pptImageList.size());
                                if (pptImageList.size() == 0 && fileExtension.compareToIgnoreCase("pptx") == 0) {
//                                    logger.info("no image yet,lets generate");
                                    pptImageList = ParseToImage(fileName);
//                                    logger.info("image generated : " + pptImageList.size());
                                }
//                                logger.info("image already generated, count :" + pptImageList.size());
                                urlDownload.addAll(pptImageList);

                            } else {
                                urlDownload.add("/resource/downloadResource/" + resource.getFile());
                            }
                        }

                        resultResourceMap.put("url_download", urlDownload);
                        resourcesResult.add(resultResourceMap);
                    }
                    resultPlayListMap.put("resources", resourcesResult);

                    playlistResult.add(resultPlayListMap);
                }


                resultData.put("playlist", playlistResult);
                results.add(resultData);
            }
            if (results.size() > 0) {
                response.setData(results);
                response.setStatus("200");
                response.setSuccess(true);
                response.setMessage("Playlist Listed");
            } else {
                response.setStatus("404");
                response.setSuccess(false);
                response.setMessage("No playlist found for this device");
            }


        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    //    public List<Playlist> getPlaylistByName(String playlist_name) {
//        List<Playlist> getPlaylistResource = new ArrayList<>();
//        getPlaylistResource = playlistRepository.getPlaylistByName(playlist_name);
//        return getPlaylistResource;
//    }

    //PLAYLIST-RESOURCE SECTION
    public BaseResponse<String> addPlaylistResource(String userToken, int resource_id, int playlist_id, int order, int position_id) throws
            Exception {
        BaseResponse response = new BaseResponse();
        try {
            Map<String, Object> auth = tokenAuthentication(userToken);
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            PlaylistResource playlistResource = new PlaylistResource();
            playlistResource.setPlaylist_id(playlist_id);
            playlistResource.setPosition_id(position_id);
            playlistResource.setOrder(order);
            playlistResource.setResource_id(resource_id);
            playlistResource.setStatus("active");
            playlistResource.setCreated_by(userOnProcess);
            playlistResource.setCreated_date(new Date());
            playlistResource.setUpdated_by("");
            playlistResource.setUpdated_date(new Date());
            playlistResourceRepository.save(playlistResource);

//            playlistResourceRepository.save(playlist_id, resource_id, order, userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Playlist add resource successfully");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getPlaylistResource(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        int playlist_id;
        try {
            jsonInput = new JSONObject(input);
            playlist_id = jsonInput.optInt("playlist_id");
            List<Playlist> playlistList = playlistRepository.getPlaylistById(playlist_id);
            List<Position> positionList = positionRepository.getPositionByProfileId(playlistList.get(0).getProfile_id());
            for (Position position : positionList) {
                Map resultMap = new HashMap();
                resultMap.put("position_id", position.getPosition_id());
                resultMap.put("position_name", position.getBox());
                List<PlaylistResource> getPlaylistResource = playlistResourceRepository.getPlaylistResourceByPlaylistAndPosition(playlist_id, position.getPosition_id());
                List resource_list = new ArrayList();
                for (int i = 0; i < getPlaylistResource.size(); i++) {
                    Map resourceMap = new HashMap();
                    List<Resource> resources = resourceRepository.getResourceById(getPlaylistResource.get(i).getResource_id());
                    resourceMap.put("resource_id", resources.get(0).getResource_id());
                    resourceMap.put("resource_name", resources.get(0).getResource_name());
                    resourceMap.put("playlist_resource_id", getPlaylistResource.get(i).getPlaylist_resource_id());
                    resourceMap.put("order", getPlaylistResource.get(i).getOrder());
                    resource_list.add(resourceMap);
                }
                resultMap.put("resource_list", resource_list);
                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("PlaylistResource Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<PlaylistResource> updatePlaylistResource(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            JSONArray playlistResourceItem = jsonInput.getJSONArray("data");
            for (int i = 0; i < playlistResourceItem.length(); i++) {
                JSONObject obj = playlistResourceItem.getJSONObject(i);
                playlistResourceRepository.updatePlaylistResource(obj.optInt("order"), obj.optInt("playlist_resource_id"), userOnProcess);
            }
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("PlaylistResource successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<PlaylistResource> deletePlaylistResource(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            JSONArray playlistResourceItem = jsonInput.getJSONArray("data");
            for (int i = 0; i < playlistResourceItem.length(); i++) {
                JSONObject obj = playlistResourceItem.getJSONObject(i);
                playlistResourceRepository.deletePlaylistResource(obj.optInt("playlist_resource_id"));
            }

            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("PlaylistResource successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    //RUNNING TEXT SECTION
    public BaseResponse<String> addNewRunningText(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        String user_token;
        String running_text;
        int branch_id;
        int region_id;
        int company_id;
        String start_date;
        String end_date;
        String tittle;
        String description;

        try {
            JSONObject jsonInput = new JSONObject(input);
            user_token = jsonInput.optString("user_token");
            Map<String, Object> auth = tokenAuthentication(user_token);
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            tittle = jsonInput.optString("tittle");
            description = jsonInput.optString("description");
            running_text = jsonInput.optString("running_text");
            branch_id = jsonInput.optInt("branch_id");
            region_id = jsonInput.optInt("region_id");
            company_id = jsonInput.optInt("company_id");
            start_date = jsonInput.optString("start_date");
            end_date = jsonInput.optString("end_date");

            //RunningText tittle  check
            List<RunningText> runningTextTittleCheckResult = runningTextRepository.getRunningTextByTittle(tittle, branch_id, region_id, company_id);
            if (runningTextTittleCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("RunningText Tittle already exist / used");
                return response;
            }
            if (company_id == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Unknown company, please choose existing company.");
                return response;
            }

            //Save running text object
            RunningText runningText = new RunningText();
            runningText.setTittle(tittle);
            runningText.setDescription(description);
            runningText.setRunning_text(running_text);
            runningText.setCompany_id(company_id);
            runningText.setRegion_id(region_id);
            runningText.setBranch_id(branch_id);
            runningText.setStatus("active");
            runningText.setStart_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_date));
            runningText.setEnd_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end_date));
            runningText.setCreated_by(userOnProcess);
            runningText.setCreated_date(new Date());
            runningText.setUpdated_by("");
            runningText.setUpdated_date(new Date());
            runningTextRepository.save(runningText);
//            runningTextRepository.save(jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optString("tittle"), description,
//                    running_text, start_date, end_date, userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("RunningText successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getRunningTextList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String branch_id;
        String region_id;
        String company_id;
        String tittle;
        String description;
        String running_text;
        String start_date;
        String end_date;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.isEmpty() || region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.isEmpty() || company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            tittle = "%" + jsonInput.optString("tittle") + "%";
            description = "%" + jsonInput.optString("description") + "%";
            running_text = "%" + jsonInput.optString("popup_description") + "%";
            start_date = "%" + jsonInput.optString("start_date") + "%";
            end_date = "%" + jsonInput.optString("end_date") + "%";

            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<RunningText> getRunningTextResult = runningTextRepository.getRunningTextList(branch_id, region_id, company_id, tittle, description,
                    running_text, start_date, end_date, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getRunningTextResult.size(); i++) {
                Map resultMap = new HashMap();
                if (getRunningTextResult.get(i).getBranch_id() != 0) {
                    List<Branch> branch = branchRepository.getBranchById(getRunningTextResult.get(i).getBranch_id());
                    resultMap.put("branch", branch.get(0));
                } else {
                    Branch branch = new Branch();
                    branch.setBranch_id(0);
                    branch.setBranch_name("All Branches");
                    resultMap.put("branch", branch);
                }
                if (getRunningTextResult.get(i).getRegion_id() != 0) {
                    List<Region> regions = regionRepository.getRegionById(getRunningTextResult.get(i).getRegion_id());
                    resultMap.put("region", regions.get(0));
                } else {
                    Region region = new Region();
                    region.setRegion_id(0);
                    region.setRegion_name("All Regions");
                    resultMap.put("region", region);
                }
                if (getRunningTextResult.get(i).getCompany_id() != 0) {
                    List<Company> companies = companyRepository.getCompanyById(getRunningTextResult.get(i).getCompany_id());
                    resultMap.put("company", companies.get(0));
                } else {
                    resultMap.put("company", "All Companies");
                }

                resultMap.put("running_text", getRunningTextResult.get(i));

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("RunningText Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse getRunningTextAndroid(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String branch_id;
        String region_id;
        String company_id;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.isEmpty() || region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.isEmpty() || company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }


            RunningText getRunningTextResult = runningTextRepository.getRunningTextAndroid(branch_id, region_id, company_id);


            response.setData(getRunningTextResult);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("RunningText Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<RunningText> updateRunningText(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            //RunningText tittle  check
            List<RunningText> running_textTittleCheckResult = runningTextRepository.getRunningTextByTittleExceptId(jsonInput.optString("tittle"), jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optInt("running_text_id"));
            if (running_textTittleCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("RunningText Tittle already exist / used");
                return response;
            }
            if (jsonInput.optInt("company_id") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Unknown company, please choose existing company.");
                return response;
            }
            runningTextRepository.updateRunningText(jsonInput.optInt("branch_id"), jsonInput.optInt("region_id"), jsonInput.optInt("company_id"), jsonInput.optString("tittle"), jsonInput.optString("description"),
                    jsonInput.optString("running_text"), jsonInput.optString("start_date"), jsonInput.optString("end_date"),
                    jsonInput.optString("status"), userOnProcess, jsonInput.optInt("running_text_id"));
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("RunningText successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<RunningText> deleteRunningText(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            runningTextRepository.deleteRunningText(jsonInput.optInt("running_text_id"), userOnProcess);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("RunningText successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<RunningText> getRunningTextById(int running_text_id) {
        List<RunningText> getRunningTextResult = new ArrayList<>();
        getRunningTextResult = runningTextRepository.getRunningTextById(running_text_id);
        return getRunningTextResult;
    }


    //DEVICE MONITORING LOG SECTION
    public BaseResponse<List<Device>> getDeviceListFromPlaylist(String input) throws SQLException, Exception {
        BaseResponse response = new BaseResponse();
        String branch_id;
        String region_id;
        String company_id;
        String userToken;
        List<Device> deviceList;
        JSONObject jsonInput;
        try {
            jsonInput = new JSONObject(input);
            userToken = jsonInput.optString("user_token");
            Map<String, Object> auth = tokenAuthentication(userToken);

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.isEmpty() || region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.isEmpty() || company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }

            deviceList = deviceRepository.getDeviceListFromPlaylist(branch_id, region_id, company_id);

            response.setData(deviceList);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Device Listed");
        } catch (Exception e) {
            response.setData(new ArrayList<>());
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse getDeviceMonitoringLog(String input) throws SQLException, Exception {
        BaseResponse response = new BaseResponse();
        Map<String, Object> result = new HashMap<>();
        String userToken;
        int device_id;
        int limit = 0;
        int offset = 0;
        int startingData = 0;
        List<DeviceMonitoringLog> logList;
        List<DeviceMonitoringLog> logListPaged = new ArrayList<>();
        JSONObject jsonInput;
        try {
            jsonInput = new JSONObject(input);
            userToken = jsonInput.optString("user_token");
            Map<String, Object> auth = tokenAuthentication(userToken);
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            device_id = jsonInput.optInt("device_id");
            limit = jsonInput.getInt("limit");
            offset = jsonInput.getInt("offset");
            startingData = (offset - 1) * limit;
            logList = deviceMonitoringLogRepository.getDeviceMonitoringLogList(device_id);
            int maxPage = (int) Math.ceil(logList.size() / (limit * 1.0));
            if (logList.size() > 0) {
                logListPaged = logList.subList(startingData, Math.min((startingData + limit), logList.size()));
            }

            for (DeviceMonitoringLog deviceMonitoringLog : logListPaged) {
                try {
                    deviceMonitoringLog.setLog_screenshot_path(getFileDirectPath(deviceMonitoringLog.getLog_screenshot_path()).getData().get("file_base64").toString());
                } catch (Exception e) {
                    deviceMonitoringLog.setLog_screenshot_path("");
                }
            }
            result.put("logData", logListPaged);
            result.put("maxPage", maxPage);

            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Device log listed");
        } catch (Exception e) {
            response.setData(new ArrayList<>());
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    //LICENSE KEY SECTION
    public BaseResponse<String> addNewLicenseKey(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        int count = 0;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            int company_id = (int) auth.get("company_id");

//            String userOnProcess = auth.get("user_name").toString();
//            Rfc2898DeriveBytes pdb = new Rfc2898DeriveBytes(EncryptionKey, new byte[] { 0x49, 0x76, 0x61, 0x6e, 0x20, 0x4d, 0x65, 0x64, 0x76, 0x65, 0x64, 0x65, 0x76 });

            String fileBase64 = jsonInput.getString("file");
            String line = "";
            List<LicenseKey> licenseKeyList = licenseRepository.findAll();
            List license = new ArrayList();
            for (LicenseKey licenseKey : licenseKeyList) {
                license.add(licenseKey.getLicense_key());
            }
            byte[] b = Base64.getMimeDecoder().decode(fileBase64);
            InputStream inputStream = new ByteArrayInputStream(b);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                if (!license.contains(line)) {
                    count++;
                    LicenseKey licenseKeys = new LicenseKey();
                    licenseKeys.setLicense_key(line);
                    licenseKeys.setCompany_id(company_id);
                    licenseRepository.save(licenseKeys);
                }
            }
            response.setStatus("200");
            response.setSuccess(true);
            if (count > 0) {
                response.setMessage(count + " - License Key successfully added");
            } else {
                response.setMessage("No new license key added, all license already registered");
            }


        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse generateLicenseKey(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            int device_id = jsonInput.getInt("device_id");


            List<Device> checkIsLicenseAlreadyGenerated = deviceRepository.getDeviceById(device_id);
            if (!checkIsLicenseAlreadyGenerated.get(0).getLicense_key().isEmpty()) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Failed to generate license key, this device already has license key");
                return response;
            }

            List<String> availableLicense = licenseRepository.getAvailableLicense();
            if (availableLicense.size() == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("No more license available, please contact application administrator");
                return response;
            }

            String licenseKey = availableLicense.get(0);
            String licenseOriginalTest = cmsEncryptDecrypt.decrypt(licenseKey);
            if (licenseOriginalTest == null) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("available license (" + licenseKey + ") not valid");
                return response;
            }

            deviceRepository.updateLicenseKey(device_id, licenseKey, userOnProcess);
            response.setData(licenseKey);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("License key generated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage("Failed to generate license key :" + e.getMessage());
        }

        return response;
    }


    //TOKEN AUTH
    public Map<String, Object> tokenAuthentication(String token) {
        Map<String, Object> result = new HashMap();
        List<Users> usersList;
        try {
            usersList = usersRepository.tokenAuth(token);
            if (usersList.size() > 0) {
                result.put("valid", true);
                result.put("user_name", usersList.get(0).getUser_name());
                result.put("company_id", usersList.get(0).getCompany_id());
            } else {
                result.put("valid", false);
                result.put("user_name", "");
                result.put("company_id", "");
            }

        } catch (Exception e) {
            result.put("valid", false);
            result.put("user_name", "");
        }
        return result;
    }


    //FILE SECTION
    public BaseResponse<Map<String, String>> addFile(String file_name, String file_content, String folder) {
        BaseResponse response = new BaseResponse();
        Session session = null;
        ChannelSftp channel = null;
        Map<String, String> imageAddResult = new HashMap<>();
        try {
            UUID uuid = UUID.randomUUID();
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            String path = "";
            if (folder.compareToIgnoreCase("promo") == 0) {
                path = attachmentPathPromo;
            } else if (folder.compareToIgnoreCase("playlist") == 0) {
                path = attachmentPathPlaylist;
            } else if (folder.compareToIgnoreCase("resource") == 0) {
                path = attachmentPathResource;
            } else {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("unknown folder");
                return response;
            }
            byte[] b = Base64.getMimeDecoder().decode(file_content);
            InputStream stream = new ByteArrayInputStream(b);
            channel.put(stream, path + uuid + "_" + file_name, 0);

//            creating thumbnail
            String[] acceptedImageType = {"JPEG", "PNG", "BMP", "WEBMP", "GIF", "JPG"};
            List<String> acceptedImage = new ArrayList(Arrays.asList(acceptedImageType));
            String[] acceptedVideoType = {"MOV", "MP4", "3GP"};
            List<String> acceptedVideo = new ArrayList(Arrays.asList(acceptedVideoType));
            String thumbnailBase64 = "";
            String thumbnailName = "";
            String fileExtension = file_name.substring(file_name.lastIndexOf(".") + 1);
            if (acceptedImage.stream().anyMatch(fileExtension::equalsIgnoreCase)) {
                logger.info("creating thumbnail for  " + fileExtension + " image");
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));
                BufferedImage resizedImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2D = resizedImage.createGraphics();
                graphics2D.drawImage(img, 0, 0, 60, 60, null);
                graphics2D.dispose();
                ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
                ImageIO.write(resizedImage, fileExtension, out);
                thumbnailBase64 = Base64.getEncoder().encodeToString(out.toByteArray());
                byte[] c = Base64.getMimeDecoder().decode(thumbnailBase64);
                InputStream streamThumbnail = new ByteArrayInputStream(c);
                channel.put(streamThumbnail, path + "thumbnail_" + uuid + "_" + file_name, 0);
                thumbnailName = "thumbnail_" + uuid + "_" + file_name;
            } else if (acceptedVideo.stream().anyMatch(fileExtension::equalsIgnoreCase)) {
                logger.info("creating thumbnail for  " + fileExtension + " video");
                thumbnailName = "thumbnail_video.png";
            } else if (fileExtension.compareToIgnoreCase("pptx") == 0 || fileExtension.compareToIgnoreCase("ppt") == 0 || fileExtension.compareToIgnoreCase("pps") == 0 || fileExtension.compareToIgnoreCase("ppsx") == 0) {
                logger.info("creating thumbnail for  " + fileExtension + " power point");
                thumbnailName = "thumbnail_ppt.png";
            } else {
                logger.info("its not an image or video, used web thumbnail");
                thumbnailName = "thumbnail_url.png";
            }

            imageAddResult.put("file", uuid + "_" + file_name);
            imageAddResult.put("thumbnail", thumbnailName);

            response.setData(imageAddResult);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("File successfully Added");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.info(new Date().getTime() + e.getMessage());
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }
        }
        return response;
    }
//    public void duplicateFile(String file_name, String file_content) throws Exception {
//        Session session = null;
//        ChannelSftp channel = null;
//        Map<String, String> imageAddResult = new HashMap<>();
//        try {
//            session = new JSch().getSession(sftpUser, sftpUrl, 22);
//            session.setPassword(sftpPassword);
//            session.setConfig("StrictHostKeyChecking", "no");
//            session.connect();
//            channel = (ChannelSftp) session.openChannel("sftp");
//            channel.connect();
//
//            byte[] b = Base64.getMimeDecoder().decode(file_content);
//            InputStream stream = new ByteArrayInputStream(b);
//            channel.put(stream, attachmentPathResource +  file_name, 0);
//
//
//        } catch (Exception e) {
//            logger.info(new Date().getTime() + e.getMessage());
//        } finally {
//            if (session.isConnected() || session != null) {
//                session.disconnect();
//            }
//            if (channel.isConnected() || channel != null) {
//                channel.disconnect();
//            }
//        }
//    }

    public BaseResponse<Map<String, Object>> getFile(String file_name, String folder) {
        BaseResponse response = new BaseResponse();
        Session session = null;
        ChannelSftp channel = null;
        Map<String, Object> result = new HashMap<>();
        try {
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            String path = "";
            if (folder.compareToIgnoreCase("promo") == 0) {
                path = attachmentPathPromo;
            } else if (folder.compareToIgnoreCase("playlist") == 0) {
                path = attachmentPathPlaylist;
            } else if (folder.compareToIgnoreCase("resource") == 0) {
                path = attachmentPathResource;
            } else {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("unknown folder");
                logger.info("unknown folder");
                return response;
            }

            InputStream inputStream = channel.get(path + file_name);
            logger.info("file path : " + path + file_name);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String base64 = Base64.getEncoder().encodeToString(bytes);

            result.put("file_byte", "-");
            result.put("file_base64", base64);

            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Get File success");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.info(new Date().getTime() + e.getMessage());
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }
        }
        return response;
    }

    public List<String> ParseToImage(String file_name) throws Exception {
        List<String> result = new ArrayList<>();
        Session session = null;
        ChannelSftp channel = null;
        try {
//            byte[] b = Base64.getMimeDecoder().decode(getFile(file_name, "resource").getData().get("file_base64").toString());
//            InputStream stream = new ByteArrayInputStream(b);
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            InputStream inputStream = channel.get(attachmentPathResource + file_name);

            logger.info("FILE LOADED INTO STREAM");

//            File file = new File("temp.pptx");
//            FileUtils.copyInputStreamToFile(stream, file);
            XMLSlideShow ppt = new XMLSlideShow(inputStream);
            inputStream.close();
            logger.info("PPT LOADED");
            String fileNameOnly = file_name.split("\\.")[0];
            logger.info("FILE NAME : " + fileNameOnly);
//
            // get the dimension and size of the slide
            Dimension pgsize = ppt.getPageSize();
            List<XSLFSlide> slide = ppt.getSlides();
            BufferedImage img = null;

            logger.info("slide size : " + slide.size());

            for (int i = 0; i < slide.size(); i++) {
                img = new BufferedImage(
                        pgsize.width, pgsize.height,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                logger.info("img create : " + i);

                // clear area
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(
                        0, 0, pgsize.width, pgsize.height));

                // draw the images
                slide.get(i).draw(graphics);
                logger.info("image draw : " + i);
                // file.getParent().mkdirs();

//                System.out.println(fileNameWithOutExt);
//                OutputStream outputStream = channel.put(attachmentPathResource + fileNameOnly + "-" + i + ".png", 0);

                String imageFileName = fileNameOnly + "-" + i + ".png";
                logger.info("image name : " + imageFileName);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(img, "png", os);
                logger.info("write to OS success : " + i);
                InputStream fis = new ByteArrayInputStream(os.toByteArray());
                logger.info("IS created from OS : " + i);
                channel.put(fis, attachmentPathResource + imageFileName, 0);
                logger.info("image name generated : " + imageFileName);
                result.add("/resource/downloadResource/" + imageFileName);

//                FileOutputStream out = new FileOutputStream(
//                        fileNameWithOutExt + "-" + i + ".png");

//                javax.imageio.ImageIO.write(img, "png", outputStream);
//                ppt.write(outputStream);
//                outputStream.close();
//                System.out.println(i);
            }
        } catch (SftpException e) {
            logger.info("error while parsing ppt :" + e.getMessage());
            return null;
        } catch (IOException e) {
            logger.info("error while parsing ppt :" + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.info("error while parsing ppt :" + e.getMessage());
            return null;
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }
        }

        return result;
    }

    public List<String> getPresentationImage(String file_name) throws Exception {
        List<String> result = new ArrayList<>();
        Session session = null;
        ChannelSftp channel = null;
        try {
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            String searchImageName = file_name.concat("-");
            Vector<ChannelSftp.LsEntry> imageFileGenerated = channel.ls(attachmentPathResource);
            for (ChannelSftp.LsEntry entry : imageFileGenerated) {
                if (!entry.getAttrs().isDir()) {
                    if (entry.getFilename().contains(searchImageName)) {
                        result.add("/resource/downloadResource/" + entry.getFilename());
                    }
                }
            }


        } catch (SftpException e) {
            logger.info("error while get ppt image parsed :" + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.info("error while parsing ppt :" + e.getMessage());
            return null;
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }
        }

        return result;
    }

    public BaseResponse<Map<String, Object>> getFileDirectPath(String path) {
        BaseResponse response = new BaseResponse();
        Session session = null;
        ChannelSftp channel = null;
        Map<String, Object> result = new HashMap<>();
        try {
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();


            InputStream inputStream = channel.get(path);
            logger.info("file path : " + path);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String base64 = Base64.getEncoder().encodeToString(bytes);

            result.put("file_byte", "-");
            result.put("file_base64", base64);

            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Get File success");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.info(new Date().getTime() + e.getMessage());
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }
        }
        return response;
    }

    public InputStream downloadFile(String file_name, String folder) {
        Session session = null;
        ChannelSftp channel = null;
        Map<String, Object> result = new HashMap<>();
        InputStreamResource resource = null;
        org.springframework.core.io.Resource resources;
        InputStream inputStream = null;
        try {
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            String path = "";
            if (folder.compareToIgnoreCase("promo") == 0) {
                path = attachmentPathPromo;
            } else if (folder.compareToIgnoreCase("playlist") == 0) {
                path = attachmentPathPlaylist;
            } else if (folder.compareToIgnoreCase("resource") == 0) {
                path = attachmentPathResource;
            } else {
//                response.setStatus("500");
//                response.setSuccess(false);
//                response.setMessage("unknown folder");
                return null;
            }

            inputStream = channel.get(path + file_name);
            resource = new InputStreamResource(inputStream);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            logger.info("file path : " + path + file_name);

//            response.setData(result);
//            response.setStatus("200");
//            response.setSuccess(true);
//            response.setMessage("Get File success");
        } catch (Exception e) {
//            response.setStatus("500");
//            response.setSuccess(false);
//            response.setMessage(e.getMessage());
            logger.info(new Date().getTime() + e.getMessage());
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }
        }
        return inputStream;
    }

    public BaseResponse<String> uploadFile(MultipartFile obj, String folder) {
        BaseResponse response = new BaseResponse();
        Session session = null;
        ChannelSftp channel = null;
        try {
            UUID uuid = UUID.randomUUID();
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            String path = "";
            if (folder.compareToIgnoreCase("promo") == 0) {
                path = attachmentPathPromo;
            } else if (folder.compareToIgnoreCase("playlist") == 0) {
                path = attachmentPathPlaylist;
            } else if (folder.compareToIgnoreCase("resource") == 0) {
                path = attachmentPathResource;
            } else {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("unknown folder");
                return response;
            }

            InputStream stream = obj.getInputStream();
            channel.put(stream, path + uuid + "_" + obj.getOriginalFilename(), 0);

            response.setData(uuid + "_" + obj.getOriginalFilename());
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("File successfully Added");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            logger.info(new Date().getTime() + e.getMessage());
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }
        }
        return response;
    }

    //PROFILE SECTION

    public BaseResponse<String> addNewProfile(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        int company_id;
        int region_id;
        int branch_id;
        String profile_name;
        String description;
        String created_by;
        Date created_date;
        String updated_by;
        Date updated_date;
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            company_id = jsonInput.getInt("company_id");
            region_id = jsonInput.getInt("region_id");
            branch_id = jsonInput.getInt("branch_id");
            profile_name = jsonInput.optString("profile_name");
            if (profile_name.isEmpty()) {
                response.setSuccess(false);
                response.setStatus("500");
                response.setMessage("Profile name can't be empty");
                return response;
            }
            description = jsonInput.optString("description");


            //Profile_name  check
            List<Profile> profileNameCheckResult = profileRepository.getProfileByProfileName(profile_name, branch_id, region_id, company_id);
            if (profileNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Profile name already exist / used");
                return response;
            }

            if (jsonInput.optInt("company_id") == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Unknown company, please choose existing company.");
                return response;
            }
            Profile profile = new Profile();
            profile.setBranch_id(branch_id);
            profile.setRegion_id(region_id);
            profile.setCompany_id(company_id);
            profile.setProfile_name(profile_name);
            profile.setDescription(description);
            profile.setStatus_profile("active");
            profile.setStatus("active");
            profile.setCreated_by(userOnProcess);
            profile.setUpdated_by("");
            profile.setCreated_date(new Date());
            profile.setUpdated_date(new Date());
            profileRepository.save(profile);


            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Profile successfully Added");
            logger.info("Profile successfully Added");

        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage("Failed create profile : " + e.getMessage());
            logger.info("Failed create profile : {}", e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Map<String, Object>>> getProfileList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String branch_id;
        String region_id;
        String company_id;
        String profile_name;
        String description;
        String status_profile;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            branch_id = jsonInput.optInt("branch_id") + "";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("null") == 0 || branch_id.compareToIgnoreCase("0") == 0) {
                branch_id = "%%";
            }
            region_id = jsonInput.optInt("region_id") + "";
            if (region_id.isEmpty() || region_id.compareToIgnoreCase("null") == 0 || region_id.compareToIgnoreCase("0") == 0) {
                region_id = "%%";
            }
            company_id = jsonInput.optInt("company_id") + "";
            if (company_id.isEmpty() || company_id.compareToIgnoreCase("null") == 0 || company_id.compareToIgnoreCase("0") == 0) {
                company_id = "%%";
            }
            description = "%" + jsonInput.optString("description") + "%";

            status_profile = jsonInput.optString("status_profile");
            if (status_profile.isEmpty()) {
                status_profile = "%%";
            }
            profile_name = jsonInput.optString("profile_name");
            if (profile_name.isEmpty()) {
                profile_name = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Profile> getProfileResult = profileRepository.getProfileList(branch_id, region_id, company_id, profile_name, description, status_profile, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getProfileResult.size(); i++) {
                Profile profile = getProfileResult.get(i);
                List<Position> positionList = positionRepository.getPositionByProfileId(profile.getProfile_id());
                Map resultMap = new HashMap();
                if (profile.getBranch_id() != 0) {
                    List<Branch> branch = branchRepository.getBranchById(profile.getBranch_id());
                    resultMap.put("branch", branch.get(0));
                } else {
                    Branch branch = new Branch();
                    branch.setBranch_id(0);
                    branch.setBranch_name("All Branches");
                    resultMap.put("branch", branch);
                }
                if (profile.getRegion_id() != 0) {
                    List<Region> regions = regionRepository.getRegionById(getProfileResult.get(i).getRegion_id());
                    resultMap.put("region", regions.get(0));
                } else {
                    Region region = new Region();
                    region.setRegion_id(0);
                    region.setRegion_name("All Regions");
                    resultMap.put("region", region);
                }
                if (profile.getCompany_id() != 0) {
                    List<Company> companies = companyRepository.getCompanyById(getProfileResult.get(i).getCompany_id());
                    resultMap.put("company", companies.get(0));
                } else {
                    resultMap.put("company", "All Companies");
                }

                resultMap.put("positions", positionList);
                resultMap.put("profile", getProfileResult.get(i));

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Profile Listed");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Profile> updateProfile(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        int profile_id;
        String profile_name;
        String description;
        int company_id = 0;
        int region_id = 0;
        int branch_id = 0;
        String status_profile;

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            profile_id = jsonInput.getInt("profile_id");
            profile_name = jsonInput.optString("profile_name");
            status_profile = jsonInput.getString("status_profile");
            if (profile_name.isEmpty()) {
                response.setSuccess(false);
                response.setStatus("500");
                response.setMessage("Profile name can't be empty");
                return response;
            }
            description = jsonInput.optString("description");
            if (getProfileById(profile_id).size() > 0) {
                Profile profile = getProfileById(profile_id).get(0);
                branch_id = profile.getBranch_id();
                region_id = profile.getRegion_id();
                company_id = profile.getCompany_id();
            } else {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Profile with id " + profile_id + " not found");
                return response;
            }

            //Profile name check
            List<Profile> profileNameCheckResult = profileRepository.getProfileByTittleExceptId(profile_name, branch_id, region_id, company_id, profile_id);
            if (profileNameCheckResult.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Profile name already exist / used");
                return response;
            }
            if (company_id == 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Unknown company, please choose existing company.");
                return response;
            }
            profileRepository.updateProfile(profile_name, description, status_profile, userOnProcess, profile_id);
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Profile successfully Updated");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Profile> deleteProfile(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        int profile_id;

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            profile_id = jsonInput.getInt("profile_id");
            profileRepository.deleteProfile(profile_id, userOnProcess);
            List<Position> profilePosition = positionRepository.getPositionByProfileId(profile_id);
            if (profilePosition.size() > 0) {
                response.setStatus("500");
                response.setSuccess(false);
                response.setMessage("Profile still has " + profilePosition.size() + " position(s)");
                return response;
            }
            response.setStatus("200");
            response.setSuccess(true);
            response.setMessage("Profile successfully deleted");
        } catch (Exception e) {
            response.setStatus("500");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Profile> getProfileById(int profile_id) {
        List<Profile> getProfileResult = new ArrayList<>();
        getProfileResult = profileRepository.getProfileById(profile_id);
        return getProfileResult;
    }

    //CONFIGURATION SECTION

//    public BaseResponse<List<Users>> queryBuilder(String input) throws JSONException {
//        BaseResponse response = new BaseResponse();
//        JSONObject jsonObject = new JSONObject(input);
//        List<Users> result;
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Users> criteriaQuery = criteriaBuilder.createQuery(Users.class);
//        Root<Users> from = criteriaQuery.from(Users.class);
//        CriteriaQuery<Users> select = criteriaQuery.select(from);
//        TypedQuery<Users> typedQuery = entityManager.createQuery(select);
////        logger.info("typedQuery : "+ typedQuery.unwrap(org.hibernate.Query.class).getQueryString());
//
//        result = typedQuery.getResultList();
//        for (Users u : result) {
//            u.setUser_password("");
//        }
//        logger.info(result.toString());
//        response.setData(result);
//
//
//        return response;
//    }

    //SCHEDULER SECTION

    //CONFIGURATION SECTION
    public BaseResponse addConfig(String input) throws Exception {
        BaseResponse<List<Configuration>> result = new BaseResponse<>();
        JSONObject jsonInput = null;
        String configuration_name = "";
        String configuration_value = "";
        try {
            jsonInput = new JSONObject(input);
            configuration_name = jsonInput.optString("configuration_name");
            configuration_value = jsonInput.optString("configuration_value");
        } catch (JSONException e) {
            result.setStatus("0");
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        if (jsonInput != null && !configuration_name.equals("") && !configuration_value.equals("")) {
            Configuration configuration = new Configuration();
            configuration.setConfiguration_name(configuration_name);
            configuration.setConfiguration_value(configuration_value);
            configurationRepository.save(configuration);
            result.setStatus("2000");
            result.setSuccess(true);
            result.setMessage("Config added");
        } else {
            result.setStatus("0");
            result.setSuccess(false);
            result.setMessage("Some field is empty");
        }
        return result;
    }

    public BaseResponse getDbCredential() {
        BaseResponse response = new BaseResponse();
        List<Configuration> configurationList = configurationRepository.findAll();
        String db_config = "";
        try {
            for (Configuration configuration : configurationList) {
                if (configuration.getConfiguration_name().compareToIgnoreCase("db_credential") == 0) {
                    db_config = configuration.getConfiguration_value();
                }
            }
            if (db_config.isEmpty()) {
                response.setSuccess(false);
                response.setMessage("Database credential not found");
                response.setStatus("404");
                return response;
            }
            String encryptedDBCredential = cmsEncryptDecrypt.encrypt(db_config.getBytes());
            response.setData(encryptedDBCredential);
            response.setSuccess(true);
            response.setMessage("Database credential found");
            response.setStatus("200");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to get database credential : " + e.getMessage());
            response.setStatus("500");
        }
        return response;
    }

    public BaseResponse notFoundComponent(String component) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setStatus("404");
        response.setMessage(component + " Not found");

        return response;
    }
}
