package com.project.CmsApplication.Services;

import com.project.CmsApplication.Utility.DateFormatter;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.UserRole;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.CompanyRepository;
import com.google.gson.Gson;
import com.project.CmsApplication.repository.UserRoleRepository;
import com.project.CmsApplication.repository.UsersRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class CmsServices {

    Gson gson = new Gson();
    DateFormatter dateFormatter = new DateFormatter();

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UsersRepository usersRepository;

    public BaseResponse<String> addNewUserRole(String input) throws Exception, SQLException {
        BaseResponse response = new BaseResponse<>();
        try {
            JSONObject jsonInput = new JSONObject(input);
            userRoleRepository.save(jsonInput.optString("user_role_name"), jsonInput.optString("user_role_desc"), jsonInput.optString("created_by"));
            response.setStatus("2000");
            response.setSuccess(true);
            response.setMessage("User Role successfully Added");

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
        String user_role_name;
        String user_role_desc;
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
            user_role_name = "%" + jsonInput.optString("user_role_name") + "%";
            user_role_desc = "%" + jsonInput.optString("user_role_desc") + "%";
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            response.setData(userRoleRepository.getUserRoleList(user_role_name, user_role_desc, created_by, created_date, updated_by, updated_date));
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

    public BaseResponse<String> addNewUsers(String input) throws Exception {
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
            String userToken = Long.toHexString(new Date().getTime());

            usersRepository.save(jsonInput.optString("user_name"),
                    jsonInput.optString("user_password"), jsonInput.optString("user_email"),
                    jsonInput.optInt("user_role_id"), userOnProcess, userToken);
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
        String user_role_id;
        String user_status;
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
            user_name = "%" + jsonInput.optString("user_name") + "%";
            user_email = "%" + jsonInput.optString("user_email") + "%";
            user_role_id = "%" + jsonInput.optInt("user_role_id") + "%";
            if (user_role_id.compareToIgnoreCase("%0%") == 0) {
                user_role_id = "%%";
            }
            user_status = "%" + jsonInput.optString("user_status") + "%";
            created_by = "%" + jsonInput.optString("created_by") + "%";
            updated_by = "%" + jsonInput.optString("updated_by") + "%";
            List<Users> getUserResult = usersRepository.getUsersList(user_name, user_email, user_role_id, user_status, created_by, created_date, updated_by, updated_date);

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
