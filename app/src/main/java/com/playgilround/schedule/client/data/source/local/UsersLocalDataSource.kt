package com.playgilround.schedule.client.data.source.local

import android.content.Context
import com.playgilround.schedule.client.data.source.UsersDataSource
import com.playgilround.schedule.client.model.BaseResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersLocalDataSource @Inject constructor(val context: Context) : UsersDataSource {
    override fun register(userName: String, nickName: String, email: String, password: String, birth: String, language: String): Single<BaseResponse<String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun login(email: String, password: String, loginCallBack: UsersDataSource.LoginCallBack) {
    }

    override fun tokenLogin(loginCallBack: UsersDataSource.LoginCallBack) {
    }
}