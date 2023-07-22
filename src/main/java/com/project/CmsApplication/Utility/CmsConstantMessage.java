package com.project.CmsApplication.Utility;


import org.springframework.stereotype.Component;


@Component
public class CmsConstantMessage {

    public static final String SUCCESS_MESSAGE = "SUCCESS";
    public static final String FAILED_MESSAGE = "FAILED";


    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String CREATE_USER = "CREATE_USER";
    public static final String UPDATE_USER = "UPDATE_USER";
    public static final String DELETE_USER = "DELETE_USER";
    public static final String FORGET_PASSWORD = "FORGET_PASSWORD";


    public static final String CREATE_ROLE = "CREATE_ROLE";
    public static final String UPDATE_ROLE = "UPDATE_ROLE";
    public static final String DELETE_ROLE = "DELETE_ROLE";


    public static final String UPDATE_PRIVILEGE = "UPDATE_PRIVILEGE";


    public static final String CREATE_COMPANY = "CREATE_COMPANY";
    public static final String UPDATE_COMPANY = "UPDATE_COMPANY";
    public static final String DELETE_COMPANY = "DELETE_COMPANY";

    public static final String CREATE_REGION = "CREATE_REGION";
    public static final String UPDATE_REGION = "UPDATE_REGION";
    public static final String DELETE_REGION = "DELETE_REGION";


    public static final String CREATE_BRANCH = "CREATE_BRANCH";
    public static final String UPDATE_BRANCH = "UPDATE_BRANCH";
    public static final String DELETE_BRANCH = "DELETE_BRANCH";


    public static final String CREATE_DEVICE = "CREATE_DEVICE";
    public static final String UPDATE_DEVICE = "UPDATE_DEVICE";
    public static final String DELETE_DEVICE = "DELETE_DEVICE";
    public static final String GENERATE_DEVICE_LICENSE = "GENERATE_DEVICE_LICENSE";
    public static final String CHECK_DEVICE_UNIQUE_ID = "CHECK_DEVICE_UNIQUE_ID";
    public static final String DEVICE_LICENSE_AUTHENTICATION = "DEVICE_LICENSE_AUTHENTICATION";

    public static final String CREATE_PROFILE = "CREATE_PROFILE";
    public static final String UPDATE_PROFILE = "UPDATE_PROFILE";
    public static final String DELETE_PROFILE = "DELETE_PROFILE";

    public static final String CREATE_POSITION = "CREATE_POSITION";
    public static final String UPDATE_POSITION = "UPDATE_POSITION";
    public static final String DELETE_POSITION = "DELETE_POSITION";

    public static final String CREATE_RESOURCE = "CREATE_RESOURCE";
    public static final String UPDATE_RESOURCE = "UPDATE_RESOURCE";
    public static final String DELETE_RESOURCE = "DELETE_RESOURCE";

    public static final String CREATE_PLAYLIST = "CREATE_PLAYLIST";
    public static final String UPDATE_PLAYLIST = "UPDATE_PLAYLIST";
    public static final String DELETE_PLAYLIST = "DELETE_PLAYLIST";

    public static final String CREATE_RUNNING_TEXT = "CREATE_RUNNING_TEXT";
    public static final String UPDATE_RUNNING_TEXT = "UPDATE_RUNNING_TEXT";
    public static final String DELETE_RUNNING_TEXT = "DELETE_RUNNING_TEXT";

    public static final String ADD_NEW_LICENSE_FILE = "ADD_NEW_LICENSE_FILE";

    public static final String TOKEN_AUTHENTICATION = "TOKEN_AUTHENTICATION";


    //ERROR MESSAGE

    public static final String STATUS_IS_EMPTY = "Status must be filled, can't be empty";

    public static final String USER_NOT_EXIST = "Failed to login. User no longer exist";
    public static final String WRONG_USER_NAME_OR_PASSWORD = "Failed to login. wrong User Name, Email, or Password";

    public static final String USER_NAME_ALREADY_EXIST = "User name already exist / used";

    public static final String USER_ROLE_NULL = "User role can't be empty";

    public static final String TOKEN_AUTHENTICATION_FAILED = "Token Authentication Failed";

    public static final String USER_CREATED = "User successfully Added";
    public static final String USER_UPDATED = "User successfully Updated";
    public static final String USER_DELETED = "User successfully deleted";

    public static final String ROLE_CREATED = "Role successfully Added";
    public static final String ROLE_NAME_EXIST = "Role name already exist / used";

    public static final String ROLE_UPDATED = "Role successfully Updated";

    public static final String ROLE_STILL_USED = "The role still used by one or more user(s)";

    public static final String ROLE_DELETED = "Role successfully deleted";


    public static final String PRIVILEGE_UPDATED = "Privilege successfully Updated";


    public static final String COMPANY_NAME_NULL = "Company name can't be empty";

    public static final String COMPANY_NAME_EXIST = "Company name already exist / used";

    public static final String COMPANY_CREATED = "Company successfully Added";

    public static final String COMPANY_UPDATED = "Company successfully Updated";
    public static final String COMPANY_STILL_USED = "The company still used by one or more user(s)";

    public static final String COMPANY_DELETED = "Company successfully deleted";


    public static final String REGION_NAME_NULL = "Region name can't be empty";

    public static final String REGION_COMPANY_IS_NULL = "Company can't be empty";

    public static final String REGION_NAME_EXIST = "Region name already exist / used";

    public static final String REGION_CREATED = "Region successfully Added";

    public static final String REGION_UPDATED = "Region successfully Updated";

    public static final String REGION_STILL_USED = "The Region still has one or more branch(es)";

    public static final String REGION_DELETED = "Region successfully deleted";

    public static final String BRANCH_NAME_NULL = "Branch name can't be empty";

    public static final String BRANCH_REGION_CANT_ALL = "Can't select all, please select specific region";
    public static final String BRANCH_REGION_IS_NULL = "Region can't be empty";
    public static final String BRANCH_REGION_NOT_FOUND = "Region can't be found";

    public static final String BRANCH_NAME_EXIST = "Branch name already exist / used";

    public static final String BRANCH_CREATED = "Branch successfully Added";

    public static final String BRANCH_UPDATED = "Branch successfully Updated";

    public static final String BRANCH_STILL_USED = "The Branch still has one or more playlist(s)";

    public static final String BRANCH_DELETED = "Branch successfully deleted";

    public static final String DEVICE_NAME_EXIST = "Device name already exist / used";
    public static final String DEVICE_NAME_NULL = "Device name can't be empty";

    public static final String DEVICE_COMPANY_NOT_FOUND = "Region can't be found";

    public static final String DEVICE_CREATED = "Device successfully Added";

    public static final String DEVICE_UPDATED = "Device successfully Updated";

    public static final String DEVICE_DELETED = "Device successfully deleted";

    public static final String DEVICE_REGISTERED = "Device already registered";
    public static final String DEVICE_NOT_REGISTERED = "Device is not registered yet, please enter security code";


    public static final String LICENSE_USED_ON_MULTIPLE_DEVICE = "Cannot use the license, multiple device found with this license";

    public static final String LICENSE_INVALID = "Wrong license key or license key already used";
    public static final String DEVICE_AUTHENTICATED_REGISTERED = "Device successfully registered";


    public static final String PROFILE_NAME_NULL = "Profile name can't be empty";

    public static final String PROFILE_NAME_EXIST = "Profile name already exist / used";

    public static final String PROFILE_CREATED = "Profile successfully Added";

    public static final String PROFILE_NOT_FOUND = "Profile with not found";

    public static final String PROFILE_UPDATED = "Profile successfully Updated";

    public static final String PROFILE_STILL_USED = "Profile still has one or more position(s)";

    public static final String PROFILE_DELETED = "Profile successfully deleted";
    public static final String COMPANY_UNKNOWN = "Unknown company, please choose existing company";


    public static final String POSITION_CREATED = "Position successfully Added";

    public static final String POSITION_NOT_FOUND = "Cant' find position id";
    public static final String POSITION_UPDATED = "Position successfully Updated";
    public static final String POSITION_STILL_USED = "Position still used on  active profile(s)";

    public static final String POSITION_DELETED = "Position successfully deleted";


    public static final String RESOURCE_NAME_NULL = "Resource name can't be empty";

    public static final String RESOURCE_NAME_EXIST = "Resource name already exist / used";

    public static final String RESOURCE_CREATED = "Resource successfully Added";

    public static final String RESOURCE_UPDATED = "Resource successfully Updated";

    public static final String RESOURCE_STILL_USED = "Resource still used on one or more playlist(s)";

    public static final String RESOURCE_DELETED = "Resource successfully deleted";


    public static final String PLAYLIST_NAME_EXIST = "Can't create playlist : Playlist name already exist / used";

    public static final String PLAYLIST_EXIST_ON_PERIOD = "Can't create playlist : Playlist already exist for that period of time (select another start date /end date)";

    public static final String PLAYLIST_DEFAULT_NULL = "Is default is empty, Playlist must define as default or not";
    public static final String PLAYLIST_DEFAULT_EXIST = "Can't create playlist : default playlist already exist for this profile";
    public static final String PLAYLIST_CREATED = "Playlist successfully Added";

    public static final String PLAYLIST_PROFILE_NOT_FOUND = "Can't update playlist : profile for this playlist not found";

    public static final String PLAYLIST_UPDATED = "Playlist successfully Updated";

    public static final String PLAYLIST_DELETED = "Playlist successfully deleted";

    public static final String RUNNING_TEXT_TITTLE_EXIST = "RunningText Tittle already exist / used";

    public static final String RUNNING_TEXT_CREATED = "RunningText successfully Added";
    public static final String RUNNING_TEXT_UPDATED = "RunningText successfully Updated";
    public static final String RUNNING_TEXT_DELETED = "RunningText successfully deleted";
}
