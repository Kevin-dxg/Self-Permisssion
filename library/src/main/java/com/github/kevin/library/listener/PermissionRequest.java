package com.github.kevin.library.listener;

public interface PermissionRequest {

    //继续请求接口，用户拒绝一次后，给出Dialog提示
    void proceed();

}
