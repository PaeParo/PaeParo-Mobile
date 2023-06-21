package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.paeparo.paeparo_mobile.adapter.PlanAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityPlanBinding
import com.paeparo.paeparo_mobile.model.Event
import com.paeparo.paeparo_mobile.model.PlanViewModel
import com.paeparo.paeparo_mobile.model.Trip
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

import java.time.LocalDate

/*
    일정 세부정보를 일자별로(Day1,Day2) 보는 Activiy
 */

class PlanActivity : AppCompatActivity() {

    private lateinit var getResult: ActivityResultLauncher<Intent> // Event 생성용

    private lateinit var planAdapter: PlanAdapter
    private val binding: ActivityPlanBinding by lazy {
        ActivityPlanBinding.inflate(layoutInflater)
    }
    private val model: PlanViewModel by lazy {
        ViewModelProvider(this)[PlanViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initalizeLocalProperty()
        binding.ciPlanLoading.visibility = View.GONE

        bind()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                model.viewMode.collect {
                    with(binding) {
                        // inivisible all Btn
                        btnStartEditMode.visibility = View.GONE
                        btnEndEditMode.visibility = View.GONE
                        btnAddToMyPlan.visibility = View.GONE
                        btnAddPost.visibility = View.GONE

                        when (it) {
                            PlanViewModel.MODE.VIEW -> {
                                btnStartEditMode.visibility = View.VISIBLE
                                btnPlanFab.visibility = View.INVISIBLE
                            }
                            PlanViewModel.MODE.EDIT -> {
                                Toast.makeText(this@PlanActivity, "일정을 삭제하려면 왼쪽으로 슬라이드 해주세요.", Toast.LENGTH_SHORT).show()
                                btnEndEditMode.visibility = View.VISIBLE
                                btnPlanFab.visibility = View.VISIBLE
                            }
                            PlanViewModel.MODE.CLONE -> btnAddToMyPlan.visibility = View.VISIBLE
                            PlanViewModel.MODE.POST -> btnAddPost.visibility = View.VISIBLE
                        }
                    }
                }
                }
                launch {
                    model.eventMap.collect{
                        planAdapter.notifyDataSetChanged()
                }
            }

            }
        }


    }

    private fun initalizeLocalProperty() {
        // initaize Trip, ViewModel
        val trip = getTripFromIntent()
        if (trip == null) {
            Toast.makeText(this, "여행 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        model.initViewModelByTrip(trip!!)
        initViewMode()
        planAdapter = PlanAdapter(this@PlanActivity)
        // 이벤트 생성 결과 콜백
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                model.initViewModelByTrip(trip)
            }
        }
    }


    private fun bind() {
        with(binding) {
            tvPlanTitle.text = model.trip.name
            tvPlanSubtitle.text = model.trip.region
            vpPlan.adapter = planAdapter
//            vpPlan.offscreenPageLimit = 15

            // TabBar
            TabLayoutMediator(tlPlan, vpPlan) { tab, position ->
                tab.text = "DAY ${position + 1}"
            }.attach()

            // add Event
            btnPlanFab.setOnClickListener {
                val mIntent = Intent(this@PlanActivity, MapActivity::class.java)
                mIntent.putExtra("create_date",model.localDateList[vpPlan.currentItem])
                mIntent.putExtra("trip_id",model.trip.tripId.toString())
                getResult.launch(mIntent)
            }

            // 뒤로가기 버튼
            btnPlanBack.setOnClickListener {
                finish()
            }


            // 수정 버튼 (누를시 수정모드로 바뀜 현재상태 : 보기모드)
            btnStartEditMode.setOnClickListener {
                model.changeViewMode(PlanViewModel.MODE.EDIT)
            }


            // 보기 버튼 (누를시 보기모드로 바뀜, 현재상태 : 수정모드)
            btnEndEditMode.setOnClickListener {
                model.changeViewMode(PlanViewModel.MODE.VIEW)

            }

            // 일정에 추가 버튼
            btnAddToMyPlan.setOnClickListener {
                //TODO 시작 Date받아야됨.
                //FirebaseManager.cloneTrip(trip,this@PlanActivity.getPaeParo().userId().toString(), )
                Toast.makeText(this@PlanActivity, "일정에 추가되었습니다.", Toast.LENGTH_SHORT).show()
            }

            // 포스트 작성 버튼
            btnAddPost.setOnClickListener {
                model.changeViewMode(PlanViewModel.MODE.POST)

                val intent = Intent(this@PlanActivity, CommunityWriteActivity::class.java)
                finish()
                startActivity(intent)
            }

        }
    }

    /**
     * Check view mode
     * ViewMode를 체크한 후, 초기화
     */
    private fun initViewMode() {
        val userId = this.getPaeParo().userId.toString()

        // 여행 맴버가 아닌 경우,
        if (!model.trip.members.contains(userId)) {
            // 일정 복사 버튼 활성화
            model.changeViewMode(PlanViewModel.MODE.CLONE)
        } else {                // 일정 맴버에 추가되어 있을 경우,
            // 일정이 종료 되었을 경우,
            if (model.trip.status == Trip.TripStatus.FINISHED) {
                model.changeViewMode(PlanViewModel.MODE.POST) // 글 작성 버튼 활성화
            } else {// 진행중인 or 예정된 일정일 경우,
                model.changeViewMode(PlanViewModel.MODE.VIEW) // 읽기 모드로 시작
            }
        }

    }

    /**
     * Trip 객체를 Intent에서 가져오는 함수
     *
     * @return Trip : intent에 포함된 trip / null : tripBundle은 존재하나 tripBundle속 getParcelable를 가져올 수 없을 떄
     */
    private fun getTripFromIntent(): Trip? {
        val bundle = intent.getBundleExtra("tripBundle") ?: return null // bundle is Empty
        // Android 13
        val trip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(
                "trip",
                Trip::class.java
            )
        }
        // Android 13 보다 낮은 버전
        else {
            bundle.getParcelable<Trip>("trip")
        }
        return trip
    }

}


