package com.project.CmsApplication.Services;

import com.project.CmsApplication.Utility.DateFormatter;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Role;
import com.project.CmsApplication.model.UserRole;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.CompanyRepository;
import com.google.gson.Gson;
import com.project.CmsApplication.repository.RoleRepository;
import com.project.CmsApplication.repository.UserRoleRepository;
import com.project.CmsApplication.repository.UsersRepository;
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
            for (int i = 0; i < dataLoginUser.size(); i++) {
                dataLoginUser.get(i).setUser_password("null");
            }
            List<Integer> userRoleId = getUserRoleByUserId(dataLoginUser.get(0).getUser_id());
            for (int i = 0; i < userRoleId.size(); i++) {
                int current_id = userRoleId.get(i);
                Map user_role_name = new HashMap();
                user_role_name.put("role_id", current_id);
                user_role_name.put("role_name", getRoleById(current_id));
                user_role.add(user_role_name);
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

            userRoleRepository.save(jsonInput.optString("user_id"), jsonInput.optString("role_id"), userOnProcess);
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
