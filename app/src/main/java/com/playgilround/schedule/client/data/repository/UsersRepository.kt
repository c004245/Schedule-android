package com.playgilround.schedule.client.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.facebook.CallbackManager
import com.nhn.android.naverlogin.OAuthLogin
import com.playgilround.schedule.client.data.User
import com.playgilround.schedule.client.data.source.UsersDataSource
import com.playgilround.schedule.client.data.source.network.UsersRemoteDataSource
import com.playgilround.schedule.client.model.BaseResponse
import io.reactivex.Single
import java.util.*

class UsersRepository(
        private val usersLocalDataSource: UsersDataSource,
        private val usersRemoteDataSource: UsersRemoteDataSource) : UsersDataSource, UsersDataSource.SNSLogin {

    override fun getCurrentUser(context: Context): User? {
        return usersLocalDataSource.getCurrentUser(context)
    }

    override fun login(email: String, password: String, loginCallBack: UsersDataSource.LoginCallBack) {
        usersRemoteDataSource.login(email, password, loginCallBack)
    }

    override fun tokenLogin(loginCallBack: UsersDataSource.LoginCallBack) {
        usersRemoteDataSource.tokenLogin(loginCallBack)
    }

    override fun facebookLogin(activity: Activity, loginCallBack: UsersDataSource.LoginCallBack): CallbackManager {
        return usersRemoteDataSource.facebookLogin(activity, loginCallBack)
    }

    override fun naverInit(): OAuthLogin {
        return usersRemoteDataSource.naverInit()
    }

    override fun naverLogin(activity: Activity, oAuthLogin: OAuthLogin, loginCallBack: UsersDataSource.LoginCallBack) {
        usersRemoteDataSource.naverLogin(activity, oAuthLogin, loginCallBack)
    }

    override fun kakaoLogin(activity: Activity, loginCallBack: UsersDataSource.LoginCallBack) {
        usersRemoteDataSource.kakaoLogin(activity, loginCallBack)
    }

    override fun googleLogin(): Intent {
        return usersRemoteDataSource.googleLogin()
    }

    override fun firebaseAuthGoogle(data: Intent, loginCallBack: UsersDataSource.LoginCallBack) {
        usersRemoteDataSource.firebaseAuthGoogle(data, loginCallBack)
    }

    override fun register(userName: String,
                          nickName: String,
                          email: String,
                          password: String,
                          birth: String,
                          language: String): Single<BaseResponse<String>> {
        return usersRemoteDataSource.register(userName, nickName, email, password, birth, language)
    }

}