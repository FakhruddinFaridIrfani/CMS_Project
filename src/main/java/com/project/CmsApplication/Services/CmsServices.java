package com.project.CmsApplication.Services;

import com.project.CmsApplication.Utility.DateFormatter;
import com.project.CmsApplication.model.*;
import com.project.CmsApplication.repository.*;
import com.google.gson.Gson;
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
            role_id = "%" + jsonInput.optInt("role_id") + "%";
            status = jsonInput.optString("status");
            if (status.isEmpty()) {
                status = "%%";
            }
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            response.setData(userRoleRepository.getUserRoleList(user_id, role_id, status, created_by,
                    created_date, updated_by, updated_date));
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
                resultMap.put("company", company.get(0).getCompany_name());

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
            regionRepository.updateRegion(jsonInput.optString("region_name"), jsonInput.optString("status"), userOnProcess, jsonInput.optInt("region_id"));
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

    public List<Region> getRegionById(int company_id) {
        List<Region> getCompanyResult = new ArrayList<>();
        getCompanyResult = regionRepository.getRegionById(company_id);
        return getCompanyResult;
    }

    public List<Region> getRegionByName(String region_name) {
        List<Region> getCompanyResult = new ArrayList<>();
        getCompanyResult = regionRepository.getRegionByName(region_name);
        return getCompanyResult;
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
