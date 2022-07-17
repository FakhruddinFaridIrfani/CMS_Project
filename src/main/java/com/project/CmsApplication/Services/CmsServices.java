package com.project.CmsApplication.Services;

import com.project.CmsApplication.Utility.DateFormatter;
import com.project.CmsApplication.model.*;
import com.project.CmsApplication.repository.*;
import com.google.gson.Gson;
import javafx.geometry.Pos;
import org.apache.catalina.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

@Service
public class CmsServices {

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
    DeviceRepository deviceRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;


    //ROLE SECTION
    public BaseResponse<String> addNewRole(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            roleRepository.save(jsonInput.optString("role_name"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Role successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
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
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Role Listed");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            roleRepository.updateRole(jsonInput.optString("role_name"), jsonInput.optString("status"),
                    userOnProcess, jsonInput.optInt("role_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Role successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            roleRepository.deleteRole(jsonInput.optInt("role_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Role successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public String getRoleById(int role_id) {

        List<Role> roleList = roleRepository.getRoleById(role_id);
        String role_name = roleList.get(0).getRole_name();

        return role_name;
    }


    //USERS SECTION
    public BaseResponse<String> addNewUsers(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            String userToken = Long.toHexString(new Date().getTime());

            //user_name check
            List<Users> userNameCheckResult = usersRepository.getUsersByName(jsonInput.optString("user_name"));
            if (userNameCheckResult.size() > 0) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("User name already exist / used");
                return response;
            }

            usersRepository.save(jsonInput.optString("user_name"),
                    jsonInput.optString("user_password"), jsonInput.optString("user_email"),
                    jsonInput.optString("user_full_name"), userOnProcess, userToken);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Users>> getUsers(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
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
                response.setStatus("0");
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
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Users> getUserResult = usersRepository.getUsersList(user_name, user_email, status, user_full_name, created_by, created_date, updated_by, updated_date);
            for (int i = 0; i < getUserResult.size(); i++) {
                getUserResult.get(i).setUser_password("null");
            }
            response.setData(getUserResult);

            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User Listed");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            usersRepository.updateUser(jsonInput.optString("user_name"), jsonInput.optString("user_email"),
                    jsonInput.optString("user_status"), jsonInput.optString("user_full_name"),
                    userOnProcess, jsonInput.optInt("user_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User successfully Updated");


        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            usersRepository.deleteUser(jsonInput.optInt("user_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Map<String, Object>> loginUser(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();
        Map<String, Object> result = new HashMap<>();
        List<Users> dataLoginUser;
        List<Map> user_role = new ArrayList<>();

        try {
            JSONObject jsonInput = new JSONObject(input);
            //user status check
            List<Users> userNameCheckResult = usersRepository.getUsersByName(jsonInput.optString("user_name"));
            if (userNameCheckResult.size() > 0) {
                if (userNameCheckResult.get(0).getStatus().compareToIgnoreCase("active") != 0) {
                    response.setStatus("0");
                    response.setSuccess(false);
                    response.setMessage("Failed to login. User no longer exist");
                    return response;
                }
            }
            dataLoginUser = usersRepository.loginUser(jsonInput.optString("user_name"), jsonInput.optString("user_email"), jsonInput.optString("user_password"));
            if (dataLoginUser.size() == 0) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Failed to login. wrong User Name, Email, or Password");
                return response;
            }

            List<Integer> userRoleId = getUserRoleByUserId(dataLoginUser.get(0).getUser_id());
            for (int i = 0; i < userRoleId.size(); i++) {
                int current_id = userRoleId.get(i);
                Map user_role_name = new HashMap();
                user_role_name.put("role_id", current_id);
                user_role_name.put("role_name", getRoleById(current_id));
                user_role.add(user_role_name);
            }

            for (int i = 0; i < dataLoginUser.size(); i++) {
                dataLoginUser.get(i).setUser_password("null");
            }

            result.put("user_data", dataLoginUser);
            result.put("user_role", user_role);


            response.setData(result);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Login Success !!");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    //USER-ROLE SECTION
    public BaseResponse<String> addNewUserRole(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();

            userRoleRepository.save(jsonInput.optInt("user_id"), jsonInput.optInt("role_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User Role successfully created");

        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<UserRole>> getUserRole(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String user_id;
        String role_id;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            if (jsonInput.optString("created_date").length() > 0) {
                created_date = "%" + dateFormatter.formatDate(jsonInput.optString("created_date")) + "%";
            }
            if (jsonInput.optString("updated_date").length() > 0) {
                updated_date = "%" + dateFormatter.formatDate(jsonInput.optString("updated_date")) + "%";
            }
            user_id = "%" + jsonInput.optInt("user_id") + "%";
            if (user_id.isEmpty() || user_id.compareToIgnoreCase("%null%") == 0 || user_id.compareToIgnoreCase("%0%") == 0) {
                user_id = "%%";
            }
            role_id = "%" + jsonInput.optInt("role_id") + "%";
            if (role_id.isEmpty() || role_id.compareToIgnoreCase("%null%") == 0 || user_id.compareToIgnoreCase("%0%") == 0) {
                role_id = "%%";
            }
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<UserRole> userRoleList = userRoleRepository.getUserRoleList(user_id, role_id, status, created_by,
                    created_date, updated_by, updated_date);
            response.setData(userRoleList);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User Role Listed");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<Integer> getUserRoleByUserId(int user_id) {
        List<UserRole> userRoleList = userRoleRepository.getUserRoleByUserId(user_id);
        List<Integer> roleIdList = new ArrayList<>();
        for (UserRole role : userRoleList) {
            roleIdList.add(role.getRole_id());
        }
        return roleIdList;
    }

    //COMPANY SECTION
    public BaseResponse<String> addNewCompany(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            companyRepository.save(jsonInput.optString("company_name"), jsonInput.optString("company_address"),
                    jsonInput.optString("company_phone"), jsonInput.optString("company_email"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Company successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
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
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Company> getCompanyResult = companyRepository.getCompanyList(company_name, company_address, company_phone, company_email,
                    status, created_by, created_date, updated_by, updated_date);
            response.setData(getCompanyResult);

            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Company Listed");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            companyRepository.updateCompany(jsonInput.optString("company_name"), jsonInput.optString("company_address"), jsonInput.optString("company_phen"),
                    jsonInput.optString("company_email"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("company_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Company successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            companyRepository.deleteCompany(jsonInput.optInt("company_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
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
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            regionRepository.save(jsonInput.optInt("company_id"), jsonInput.optString("region_name"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Region successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
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
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
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
            company_id = "%" + jsonInput.optInt("company_id") + "%";
            if (company_id.compareToIgnoreCase("%null%") == 0 || company_id.compareToIgnoreCase("%0%") == 0) {
                company_id = "%%";
            }
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Region> getRegionResult = regionRepository.getRegionList(region_name, company_id, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getRegionResult.size(); i++) {
                Map resultMap = new HashMap();
                List<Company> company = getCompanyById(getRegionResult.get(i).getCompany_id());
                resultMap.put("region", getRegionResult.get(i));
                resultMap.put("company_name", company.get(0).getCompany_name());
                resultMap.put("company_id", company.get(0).getCompany_id());

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Region Listed");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            regionRepository.updateRegion(jsonInput.optString("region_name"), jsonInput.optInt("company_id"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("region_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Region successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            regionRepository.deleteRegion(jsonInput.optInt("region_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Region successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
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

    public List<Region> getRegionByName(String region_name) {
        List<Region> getCompanyResult = new ArrayList<>();
        getCompanyResult = regionRepository.getRegionByName(region_name);
        return getCompanyResult;
    }

    //BRANCH SECTION
    public BaseResponse<String> addNewBranch(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            branchRepository.save(jsonInput.optInt("region_id"), jsonInput.optString("branch_name"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Branch successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
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
        String region_id;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
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
            region_id = "%" + jsonInput.optInt("region_id") + "%";
            if (region_id.compareToIgnoreCase("%null%") == 0 || region_id.compareToIgnoreCase("%0%") == 0) {
                region_id = "%%";
            }
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Branch> getBranchResult = branchRepository.getBranchList(branch_name, region_id, status, created_by, created_date, updated_by, updated_date);

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
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Branch Listed");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            branchRepository.updateBranch(jsonInput.optString("branch_name"), jsonInput.optInt("region_id"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("branch_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Branch successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            branchRepository.deleteBranch(jsonInput.optInt("branch_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Branch successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
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

    public List<Branch> getBranchByName(String branch_name) {
        List<Branch> getBranchResult = new ArrayList<>();
        getBranchResult = branchRepository.getBranchByName(branch_name);
        return getBranchResult;
    }

    //PROMO SECTION
    public BaseResponse<String> addNewPromo(String input) throws Exception {
        BaseResponse response = new BaseResponse();
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            promoRepository.save(jsonInput.optInt("branch_id"), jsonInput.optString("tittle"), jsonInput.optString("file"), jsonInput.optString("descriptio"),
                    jsonInput.optString("popup"), jsonInput.optString("popup_descriptio"), jsonInput.optString("start_date"), jsonInput.optString("end_date"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Promo successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
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
                response.setStatus("0");
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
            branch_id = "%" + jsonInput.optInt("branch_id") + "%";
            if (branch_id.isEmpty() || branch_id.compareToIgnoreCase("%null%") == 0 || branch_id.compareToIgnoreCase("%0%") == 0) {
                branch_id = "%%";
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
            List<Promo> getPromoResult = promoRepository.getPromoList(branch_id, tittle, file, description, popup, popup_description,
                    start_date, end_date, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getPromoResult.size(); i++) {
                Map resultMap = new HashMap();
                List<Branch> branches = getBranchById(getPromoResult.get(i).getBranch_id());
                List<Region> region = getRegionById(branches.get(0).getRegion_id());
                List<Company> company = getCompanyById(region.get(0).getCompany_id());
                resultMap.put("company_name", company.get(0).getCompany_name());
                resultMap.put("company_id", company.get(0).getCompany_id());
                resultMap.put("promo", getPromoResult.get(i));
                resultMap.put("region_name", region.get(0).getRegion_name());
                resultMap.put("region_id", region.get(0).getRegion_id());
                resultMap.put("branch_name", branches.get(0).getBranch_name());

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Promo Listed");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            promoRepository.updatePromo(jsonInput.optInt("branch_id"), jsonInput.optString("tittle"), jsonInput.optString("file"), jsonInput.optString("descriptiom"),
                    jsonInput.optString("popup"), jsonInput.optString("popup_description"), jsonInput.optString("start_date"), jsonInput.optString("end_date"),
                    jsonInput.optString("status"), userOnProcess, jsonInput.optInt("promo_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Promo successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            branchRepository.deleteBranch(jsonInput.optInt("branch_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Branch successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
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
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            deviceRepository.save(jsonInput.optString("device_name"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Device successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
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
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
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
            device_name = "%" + jsonInput.optString("device_name") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Device> getDeviceResult = deviceRepository.getDeviceList(device_name, status, created_by, created_date, updated_by, updated_date);


            response.setData(getDeviceResult);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Device Listed");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Device> updateDevice(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            deviceRepository.updateDevice(jsonInput.optString("device_name"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("device_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Device successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            deviceRepository.deleteDevice(jsonInput.optInt("device_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Device successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
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
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            positionRepository.save(jsonInput.optInt("device_id"), jsonInput.optString("box"), jsonInput.optString("x_pos"), jsonInput.optString("y_pos"),
                    jsonInput.optString("width"), jsonInput.optString("height"), jsonInput.optString("measurement"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Position successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
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
        String device_id;
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
                response.setStatus("0");
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

            device_id = "%" + jsonInput.optInt("device_id") + "%";
            if (device_id.isEmpty() || device_id.compareToIgnoreCase("%null%") == 0 || device_id.compareToIgnoreCase("%0%") == 0) {
                device_id = "%%";
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
            List<Position> getPositionResult = positionRepository.getPositionList(device_id, box, x_pos, y_pos, width, height, measurement, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getPositionResult.size(); i++) {
                Map resultMap = new HashMap();
                List<Device> devices = deviceRepository.getDeviceById(getPositionResult.get(i).getDevice_id());
                resultMap.put("position", getPositionResult.get(i));
                resultMap.put("device_name", devices.get(0).getDevice_name());
                resultMap.put("device_id", devices.get(0).getDevice_id());

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Region Listed");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Position> updatePosition(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            positionRepository.updatePosition(jsonInput.optInt("device_id"), jsonInput.optString("box"), jsonInput.optString("x_pos"), jsonInput.optString("y_pos"),
                    jsonInput.optString("width"), jsonInput.optString("height"), jsonInput.optString("status"),
                    jsonInput.optString("measurement"), userOnProcess, jsonInput.optInt("position_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Position successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }


        return response;
    }

    public BaseResponse<Position> deletePosition(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            positionRepository.deletePosition(jsonInput.optInt("position_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Region successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
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
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            resourceRepository.save(jsonInput.optString("resource_name"), jsonInput.optString("type"), jsonInput.optString("thumbnail"),
                    jsonInput.optString("file"), jsonInput.optString("duration"), jsonInput.optString("stretch"), jsonInput.optString("order"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Resource successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public BaseResponse<List<Resource>> getResourceList(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        List<Map<String, Object>> result = new ArrayList<>();
        JSONObject jsonInput;
        String created_date = "%%";
        String updated_date = "%%";
        String resource_name;
        String type;
        String thumbnail;
        String file;
        String duration;
        String stretch;
        String order;
        String status;
        String created_by;
        String updated_by;
        try {
            jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
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
            duration = "%" + jsonInput.optString("duration") + "%";
            stretch = "%" + jsonInput.optString("stretch") + "%";
            order = "%" + jsonInput.optString("order") + "%";

            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Resource> getResourceResult = resourceRepository.getResourceList(resource_name, type, thumbnail, file, duration,
                    stretch, order, status, created_by, created_date, updated_by, updated_date);


            response.setData(getResourceResult);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Resource Listed");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            resourceRepository.updateResource(jsonInput.optString("resource_name"), jsonInput.optString("type"), jsonInput.optString("thumbnail"), jsonInput.optString("file"),
                    jsonInput.optString("duration"), jsonInput.optString("stretch"), jsonInput.optString("order"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("resource_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Resource successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            resourceRepository.deleteResource(jsonInput.optInt("resource_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Resource successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
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
        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));
            //Token Auth
            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            int currentSort = 1;
            List<Playlist> latestSort = playlistRepository.getSortOrder(jsonInput.optInt("position_id"), jsonInput.optInt("branch_id"));
            if (latestSort.size() > 0) {
                currentSort = latestSort.get(0).getSort() + 1;
            }
            playlistRepository.save(jsonInput.optString("playlist_name"), jsonInput.optInt("branch_id"), jsonInput.optInt("position_id"), jsonInput.optInt("resource_id"),
                    jsonInput.optString("start_date"), jsonInput.optString("end_date"), currentSort, userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Playlist successfully Added");

        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
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
        String position_id;
        String resource_id;
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
                response.setStatus("0");
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
            position_id = jsonInput.optInt("position_id") + "";
            if (position_id.isEmpty() || position_id.compareToIgnoreCase("null") == 0 || position_id.compareToIgnoreCase("0") == 0) {
                position_id = "%%";
            }
            resource_id = jsonInput.optInt("resource_id") + "";
            if (resource_id.isEmpty() || resource_id.compareToIgnoreCase("null") == 0 || resource_id.compareToIgnoreCase("0") == 0) {
                resource_id = "%%";
            }
            start_date = "%" + jsonInput.optString("start_date") + "%";
            end_date = "%" + jsonInput.optString("end_date") + "%";

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
            List<Playlist> getResultPlayList = playlistRepository.getPlaylistList(playlist_name, branch_id, position_id, resource_id, start_date,
                    end_date, sort, status, created_by, created_date, updated_by, updated_date);

            for (int i = 0; i < getResultPlayList.size(); i++) {
                Map resultMap = new HashMap();
                List<Branch> branch = branchRepository.getBranchById(getResultPlayList.get(i).getBranch_id());
                List<Position> positions = positionRepository.getPositionById(getResultPlayList.get(i).getPosition_id());
                List<Resource> resources = resourceRepository.getResourceById(getResultPlayList.get(i).getResource_id());
                List<Device> devices = deviceRepository.getDeviceById(positions.get(0).getDevice_id());
                List<Region> regions = regionRepository.getRegionById(branch.get(0).getRegion_id());
                List<Company> companies = companyRepository.getCompanyById(regions.get(0).getCompany_id());
                resultMap.put("company", companies.get(0));
                resultMap.put("region", regions.get(0));
                resultMap.put("device", devices.get(0));
                resultMap.put("position", positions.get(0));
                resultMap.put("branch", branch.get(0));
                resultMap.put("resource", resources.get(0));
                resultMap.put("playlist", getResultPlayList.get(i));

                result.add(resultMap);
            }


            response.setData(result);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Playlist Listed");
        } catch (Exception e) {
            response.setStatus("0");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public BaseResponse<Playlist> updatePlaylist(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse();

        try {
            JSONObject jsonInput = new JSONObject(input);
            Map<String, Object> auth = tokenAuthentication(jsonInput.optString("user_token"));

            if (Boolean.valueOf(auth.get("valid").toString()) == false) {
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            playlistRepository.updatePlaylist(jsonInput.optString("playlist_name"), jsonInput.optInt("branch_id"), jsonInput.optInt("position_id"),
                    jsonInput.optInt("resource_id"), jsonInput.optString("start_date"), jsonInput.optString("end_date"), jsonInput.optString("status"),
                    userOnProcess, jsonInput.optInt("playlist_id"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Playlist successfully Updated");
        } catch (Exception e) {
            response.setStatus("0");
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
                response.setStatus("0");
                response.setSuccess(false);
                response.setMessage("Token Authentication Failed");
                return response;
            }
            String userOnProcess = auth.get("user_name").toString();
            resourceRepository.deleteResource(jsonInput.optInt("playlist_id"), userOnProcess);
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("Playlist successfully deleted");
        } catch (Exception e) {
            response.setStatus("0");
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

    public List<Playlist> getPlaylistByName(String playlist_name) {
        List<Playlist> getPlaylistResource = new ArrayList<>();
        getPlaylistResource = playlistRepository.getPlaylistByName(playlist_name);
        return getPlaylistResource;
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
            } else {
                result.put("valid", false);
                result.put("user_name", "");
            }

        } catch (Exception e) {
            result.put("valid", false);
            result.put("user_name", "");
        }
        return result;
    }


}
