package com.playgilround.schedule.client.profile

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.playgilround.schedule.client.R
import com.playgilround.schedule.client.dialog.FriendDeleteDialogFragment
import kotlinx.android.synthetic.main.activity_profile.*

/**
 * 19-08-25
 * rlFriendRequest는 친구가 아니거나 tvFriendState(친구 요청),
 * 친구 요청 중 tvFriendState(친구 요청 취소)일 경우에 표시 됨.
 *
 * floatingBtn은 친구 상태 일때만 표시됨
 *
 * 해당 액티비티는 유저검색 후 유저 프로필, 내 프로필, 친구 프로필 등 유저 관련 정보 표시
 *
 */
class TestProfileActivity: AppCompatActivity() {

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation

    private var isFabOpen = false
    private var isAttention = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.floating_open)
        fabClose = AnimationUtils.loadAnimation(this, R.anim.floating_close)

        setOnClickListener()
    }

    fun setOnClickListener() {
        floatingBtn.setOnClickListener { setFloatingView() }

        llAttention.setOnClickListener {
            setAttentionFriend()
            setFloatingView()
        }

        ivAttention.setOnClickListener {
            setAttentionFriend()
            setFloatingView()
        }

        llDeleteFriend.setOnClickListener {
            showFriendDeleteDialog()
            setFloatingView()
        }
    }

    //관심 친구 설정
    private fun setAttentionFriend() {
        isAttention = if (isAttention) {
            ivAttention.visibility = View.INVISIBLE
            false
        } else {
            ivAttention.visibility = View.VISIBLE
            true
        }
    }

    //floating Animation 설정
    private fun setFloatingView() {
        isFabOpen = if (isFabOpen) {
            floatingBtn.setImageResource(R.drawable.floating_add)
            llAttention.startAnimation(fabClose)
            llDeleteFriend.startAnimation(fabClose)
            false
        } else {
            floatingBtn.setImageResource(R.drawable.floating_exit)
            llAttention.startAnimation(fabOpen)
            llDeleteFriend.startAnimation(fabOpen)
            true
        }
    }

    private fun showFriendDeleteDialog() {
        val dialogFragment = FriendDeleteDialogFragment()
        val args = Bundle()
        val content = getString(R.string.text_friend_delete_content, "한큐에 쏜다")
        args.putString(FriendDeleteDialogFragment.TITLE_KEY, getString(R.string.text_friend_delete_title))
        args.putString(FriendDeleteDialogFragment.CONTENT_KEY, content)
        dialogFragment.arguments = args
        dialogFragment.show(supportFragmentManager, "Delete Friend")
    }
}