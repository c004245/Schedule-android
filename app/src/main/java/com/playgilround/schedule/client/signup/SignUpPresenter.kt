package com.playgilround.schedule.client.signup

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.playgilround.schedule.client.ScheduleApplication
import com.playgilround.schedule.client.data.User
import com.playgilround.schedule.client.data.repository.UsersRepository
import com.playgilround.schedule.client.model.BaseResponse
import com.playgilround.schedule.client.retrofit.APIClient
import com.playgilround.schedule.client.retrofit.UserAPI
import com.playgilround.schedule.client.signup.model.UserDataModel
import com.playgilround.schedule.client.signup.view.SignUpAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SignUpPresenter constructor(mContext: Context, private val mView: SignUpContract.View, private val mUserDataModel: UserDataModel): SignUpContract.Presenter {

    companion object {
        const val ERROR_SIGN_UP = 0x0001
        const val ERROR_NETWORK_CUSTOM = 0x0002
    }

    @Inject
    internal lateinit var mUser: User

    @Inject
    internal lateinit var mUserRepository: UsersRepository
    private val mCompositeDisposable = CompositeDisposable()


    init {
        mView.setPresenter(this)
        (mContext.applicationContext as ScheduleApplication).appComponent.signUpInject(this)
    }

    override fun start() {

    }

    override fun onClickNext(position: Int) {
        var check = false

        when (position) {
            SignUpAdapter.TYPE_NAME -> {
                mUser.username = mUserDataModel.getNameField()
                check = mUser.username != null
            }

            SignUpAdapter.TYPE_EMAIL -> {
                mUser.email = mUserDataModel.getEmailField()
                check = mUser.email != null
//                check = validateEmail(mUser.email)
            }

            SignUpAdapter.TYPE_PASSWORD -> {
                mUser.password = mUserDataModel.getPasswordField()
                check = mUser.password != null
            }

            SignUpAdapter.TYPE_REPEAT_PASSWORD -> {
                mUser.password2 = mUserDataModel.getRepeatPasswordField()
                check = mUser.password2 != null
            }

            SignUpAdapter.TYPE_NICK_NAME -> {
                mUser.nickname = mUserDataModel.getNicknameField()
                check = mUser.nickname != null

//                check = validateNickName(mUser.nickname)
            }

            SignUpAdapter.TYPE_BIRTH -> {
                mUser.birth = mUserDataModel.getBirthField()
                check = mUser.birth != null
            }
        }
        mView.fieldCheck(check)
    }

    override fun onClickBack(position: Int) {
        when (position) {
            SignUpAdapter.TYPE_NAME -> mUser.username = null
            SignUpAdapter.TYPE_EMAIL -> mUser.email = null
            SignUpAdapter.TYPE_PASSWORD -> mUser.password = null
            SignUpAdapter.TYPE_REPEAT_PASSWORD -> mUser.password2 = null
            SignUpAdapter.TYPE_NICK_NAME -> mUser.nickname = null
            SignUpAdapter.TYPE_BIRTH -> mUser.birth = null
        }
    }

    override fun validateEmail(email: String): Boolean {
        val retrofit = APIClient.getClient()
        val userAPI = retrofit.create(UserAPI::class.java)
        var isValidEmail = false

        runBlocking {
            launch(Dispatchers.Default) {
                val response = userAPI.checkEmail(email).execute()
                isValidEmail = response.isSuccessful && response.body() != null
            }
        }
        return isValidEmail
    }

    override fun validateNickName(nickName: String): Boolean {
        val retrofit = APIClient.getClient()
        val userAPI = retrofit.create(UserAPI::class.java)
        var isValidNickname = false

        runBlocking {
            launch(Dispatchers.Default) {
                val response = userAPI.checkNickName(nickName).execute()
                isValidNickname = response.isSuccessful && response.body() != null
            }
        }
        return isValidNickname
    }

    override fun signUp() {
        if (mUser.password == mUser.password2) {
            mUser.password = User.base64Encoding(mUser.password)
            Log.d("TEST", "signUp password ->${mUser.password}")
        }
        val disposable = mUserRepository.register(
                mUser.username, mUser.nickname, mUser.email, mUser.password, mUser.birth, mUser.language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<BaseResponse<String>>() {
                    override fun onSuccess(t: BaseResponse<String>) {
                        Log.d("SignUp", "onSuccess Disposable")
                        mView.signUpComplete()
                    }

                    override fun onError(e: Throwable) {
                        Log.d("SignUp", "onError Disposable")
                        mView.signUpError(ERROR_SIGN_UP)
                    }
                })
        mCompositeDisposable.add(disposable)
        /*userAPI.signUp(jsonObject).enqueue(new Callback<ResponseMessage>() {
          @Override
          public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
              if (response.isSuccessful() && response.body() != null) {
                  mView.singUpComplete();
              } else {
                  CrashlyticsCore.getInstance().log(response.toString());
                  mView.signUpError(ERROR_SIGN_UP);
              }
          }

          @Override
          public void onFailure(Call<ResponseMessage> call, Throwable t) {
              CrashlyticsCore.getInstance().log(t.toString());
              mView.signUpError(ERROR_NETWORK_CUSTOM);
          }
      });*/
    }



}