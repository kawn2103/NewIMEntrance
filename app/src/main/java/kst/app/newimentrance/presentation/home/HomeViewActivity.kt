package kst.app.newimentrance.presentation.home

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.acs.bluetooth.*
import com.acs.bluetooth.BluetoothReader.ERROR_SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kst.app.newimentrance.R
import kst.app.newimentrance.databinding.ActivityHomeViewBinding
import kst.app.newimentrance.util.Utils


class HomeViewActivity: AppCompatActivity(){

    private val binding: ActivityHomeViewBinding by lazy {ActivityHomeViewBinding.inflate(layoutInflater)}

    // 블루투스 사용 어댑터
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    // 블루투스 스캐너
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    // 블루투스 패어링
    private var bluetoothGatt: BluetoothGatt? = null
    // 블루투스 연결 콜백 (acsbt 라이브러리)
    private var bluetoothReaderGattCallback: BluetoothReaderGattCallback? = null
    // 블루투스 리더 (acsbt 라이브러리)
    private var bluetoothReader: BluetoothReader? = null
    // 블루투스 리더 매니저 (acsbt 라이브러리)
    private var bluetoothReaderManager: BluetoothReaderManager? = null
    // 블루투스 스캔 플래그
    private var scanning = false
    // 블루투스 페어링 플래그
    private var pairing = false
    // 연결할 NFC 카드 리더기 디바이스 주소
    private var deviceAddress : String? = null

    // 블루투스 디바이스 탐색 결과 콜백
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("gwan2103", "result >>>>> ${result.device.name}")

            // 주변에 nfc카드 리더기가 존재 할 경우 실행
            if ( result.device.name != null && result.device.name.contains("ACR1255U-J1-")){
                // 블루투스 연결 중 멘트 변경
                CoroutineScope(Dispatchers.Main).launch {
                    binding.messageTextView.text = getString(R.string.home_view_nfc_pairing_meant_text)
                }

                // 검색한 디바이스 주소 저장
                deviceAddress = result.device.address
                //스캔 종료
                scanLeDevice()
                // NFC카드 리더기와 커넥트 진행
                connectReader()
            }

        }
    }

    // startActivityResult 대용으로 사용되는 것 특정 엑티비티의 리스폰스를 받아온 뒤 처리
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // 화면 세팅
        initView()
        // 블루투스 환경 설정
        bluetoothSetup()
        // 블루투스 리더 콜백 세팅
        bluetoothReaderGattCallbackSetup()
        // 블루투스 리더 매니저 세팅
        bluetoothReaderManagerSetup()
        // 블루투스 검색 시작
        scanLeDevice()

        if (intent.hasExtra("center_key")){
            Log.d("gwan2103", "center_key >>>>> ${intent.getStringExtra("center_key")}")
        }

    }

    // 화면 세팅
    private fun initView() {
        // 프레그먼트 세팅 시 홈화면 세팅
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.attendance_history, R.id.heat_sensing_history
            )
        )
        // 네비게이션 컨트롤러 설정
        val navigationController =
            (supportFragmentManager.findFragmentById(R.id.mainNavigationHostContainer) as NavHostFragment).navController
        binding.toolbar.setupWithNavController(navigationController, appBarConfiguration)
    }

    // 블루투스 환경 설정
    private fun bluetoothSetup(){
        // 해당 디바이스가 블루투스를 지원하는지 확인
        if (bluetoothAdapter == null) {
            // 블루투스를 지원하지 않는경우 처리 작성
        }

        // 블루투스가 켜져 있지 않을경우 처리 작성
        if (bluetoothAdapter?.isEnabled == false) {
            // 블루투스 실행 안내 팝업
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(intent)
        }

        // 블루투스 사용에 관한 런타임 권한 받아오기
    }

    // 블루투스 검색
    private fun scanLeDevice() {
        // 블루투스 핸들러
        if (!scanning) {
            // Ble 스캔중이 아닐때
            // 스캔 플래그 변경
            scanning = true
            // 블루투스 스캔 시작 뷰 변경
            CoroutineScope(Dispatchers.Main).launch {
                binding.disableView.isVisible = true
                binding.messageTextView.text = getString(R.string.home_view_nfc_searching_meant_text)
            }
            // Ble 스캔 시작
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            // ble 스캔중일때
            // 스캔 플래그 변경
            scanning = false
            // 스캔 종료
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    // 블루투스 리더 콜백 세팅
    private fun bluetoothReaderGattCallbackSetup() {
        // Gatt 콜백 생성
        bluetoothReaderGattCallback = BluetoothReaderGattCallback()
        // 블루투스 연결 상태 변경 리스너
        bluetoothReaderGattCallback?.setOnConnectionStateChangeListener { gatt, state, newState ->

            if (state != BluetoothGatt.GATT_SUCCESS){
                // 커넥션 실패 시 실행할 부분
            }

            if (newState == BluetoothProfile.STATE_CONNECTED){
                // 블루투스 연결상태로 변경 시 실행
                bluetoothReaderManager?.detectReader(gatt, bluetoothReaderGattCallback)
                // 뷰 변경
                CoroutineScope(Dispatchers.Main).launch {
                    binding.disableView.isVisible = false
                    binding.messageTextView.text = ""
                    binding.nfcReaderConnect.text = getString(R.string.home_view_nfc_on_textView_text)
                    binding.nfcReaderConnect.background = getDrawable(R.drawable.home_view_nfc_blue_background)
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                // 블루투스 끊김상태로 변경 시 실행
                //뷰 변경
                CoroutineScope(Dispatchers.Main).launch {
                    binding.nfcReaderConnect.text = getString(R.string.home_view_nfc_off_textView_text)
                    binding.nfcReaderConnect.background = getDrawable(R.drawable.home_view_nfc_red_background)
                }
                // 블루투스 상태 초기화
                bluetoothReader = null
                bluetoothGatt?.close()
                bluetoothGatt = null
                // 스캔 재 실행
                scanLeDevice()
            }

        }
    }

    // 블루투스 리더 세팅
    private fun bluetoothReaderManagerSetup(){
        // 블루투스 리더 매니저 생성
        bluetoothReaderManager = BluetoothReaderManager()
        // 블루투스 리더 매니저가 디바이스를 검색할 시 세팅 할 구문
        bluetoothReaderManager?.setOnReaderDetectionListener { reader ->
            bluetoothReader = reader
            setListener(reader)
            activateReader(reader)
        }
    }

    // 블루투스 리더를 디바이스 이름에 맞게 세팅
    private fun activateReader(reader: BluetoothReader){
        if (reader is Acr3901us1Reader){
            (bluetoothReader as Acr3901us1Reader).startBonding()
        }else if (bluetoothReader is Acr1255uj1Reader){
            bluetoothReader?.enableNotification(true)
        }
    }

    // 블루투스에서 사용될 리스너 세팅
    private fun setListener(reader: BluetoothReader){
        if (reader is Acr1255uj1Reader){
            // 배터리 변경 시 데이터를 읽어오는 리스너
            reader.setOnBatteryLevelChangeListener { reader, power ->
                Log.d(
                    "gwan2103", "setOnBatteryLevelChangeListener ///// " +
                            "reader >>>> $reader ///// battery Level >>>> $power"
                )
            }
        }

        // 인증완료 리스너
        reader.setOnAuthenticationCompleteListener { reader, errorCode ->
            if (errorCode == ERROR_SUCCESS){
                // 인증 완료 시 실행될 부분
                Log.d(
                    "gwan2103", "setOnAuthenticationCompleteListener1 ///// " +
                            "reader >>>> $reader ///// errorCode >>>> $errorCode"
                )
                // 자동 폴링 선언 (카드정보를 읽어오기 위해 필요)
                reader.transmitEscapeCommand(AUTO_POLLING_START)

            } else{
                // 인증 실패 시 실행될 부분
                Log.d(
                    "gwan2103", "setOnAuthenticationCompleteListener2 ///// " +
                            "reader >>>> $reader ///// errorCode >>>> $errorCode"
                )
            }
        }

        reader.setOnEscapeResponseAvailableListener { reader, response, errorCode ->
            Log.d(
                "gwan2103", "setOnEscapeResponseAvailableListener ///// " +
                        "reader >>>> $reader ///// response >>>> $response ///// errorCode >>>> $errorCode"
            )
        }

        // 카드 정보 리스너
        reader.setOnCardStatusChangeListener { reader, status ->
            Log.d(
                "gwan2103", "setOnCardStatusChangeListener ///// " +
                        "reader >>>> $reader ///// status >>>> $status"
            )
            val apduCommand = Utils.getStringHexBytes(APDU_CODE)
            reader.transmitApdu(apduCommand)
        }

        // 알림 완료 활성화 리스너
        reader.setOnEnableNotificationCompleteListener { reader, result ->
            Log.d(
                "gwan2103", "setOnEnableNotificationCompleteListener ///// " +
                        "reader >>>> $reader ///// result >>>> $result"
            )
            if (result != BluetoothGatt.GATT_SUCCESS){
                // 블루투스 연결 실패
            } else{
                // 블루투스 연결 성공
                val masterKey: ByteArray = Utils.getStringHexBytes(MASTER_KEY)
                reader.authenticate(masterKey)
            }

        }

        // 카드 태깅시 실행되는 리스너
        reader.setOnResponseApduAvailableListener { reader, apdu, code ->
            Log.d(
                "gwan2103", "setOnResponseApduAvailableListener ///// " +
                        "reader >>>> $reader ///// apdu >>>> $apdu ///// code >>>> $code"
            )
            val strResponseUuid = getResponseString(apdu, code)
            Log.d("gwan2103", "str_response_uuid >>>> $strResponseUuid")

        }
    }

    // 리더기와 실재로 패어링 하는 소스
    private fun connectReader(){
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null

        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)

        bluetoothGatt = device.connectGatt(this, false, bluetoothReaderGattCallback)
    }

    // 카드리더기에서 받아온 카드 정보를 변경하기 위한 소스
    private fun getResponseString(response: ByteArray?, errorCode: Int): String? {
        return if (errorCode == ERROR_SUCCESS) {
            if (response != null && response.isNotEmpty()) {
                Utils.toHexString(response)
            } else ""
        } else getErrorString(errorCode)
    }

    // 카드 리더기의 에러 스트링 정의
    private fun getErrorString(errorCode: Int): String? {
        when (errorCode) {
            ERROR_SUCCESS -> {
                return ""
            }
            BluetoothReader.ERROR_INVALID_CHECKSUM -> {
                return "The checksum is invalid."
            }
            BluetoothReader.ERROR_INVALID_DATA_LENGTH -> {
                return "The data length is invalid."
            }
            BluetoothReader.ERROR_INVALID_COMMAND -> {
                return "The command is invalid."
            }
            BluetoothReader.ERROR_UNKNOWN_COMMAND_ID -> {
                return "The command ID is unknown."
            }
            BluetoothReader.ERROR_CARD_OPERATION -> {
                return "The card operation failed."
            }
            BluetoothReader.ERROR_AUTHENTICATION_REQUIRED -> {
                return "Authentication is required."
            }
            BluetoothReader.ERROR_LOW_BATTERY -> {
                return "The battery is low."
            }
            BluetoothReader.ERROR_CHARACTERISTIC_NOT_FOUND -> {
                return "Error characteristic is not found."
            }
            BluetoothReader.ERROR_WRITE_DATA -> {
                return "Write command to reader is failed."
            }
            BluetoothReader.ERROR_TIMEOUT -> {
                return "Timeout."
            }
            BluetoothReader.ERROR_AUTHENTICATION_FAILED -> {
                return "Authentication is failed."
            }
            BluetoothReader.ERROR_UNDEFINED -> {
                return "Undefined error."
            }
            BluetoothReader.ERROR_INVALID_DATA -> {
                return "Received data error."
            }
            BluetoothReader.ERROR_COMMAND_FAILED -> {
                return "The command failed."
            }
            else -> return "Unknown error."
        }
    }

    companion object{
        private val AUTO_POLLING_START = byteArrayOf(0xE0.toByte(), 0x00, 0x00, 0x40, 0x01) // 자동폴링 시작 데이터 키
        private const val MASTER_KEY = "41435231323535552D4A312041757468"   // 마스터 키
        private const val APDU_CODE = "FFCA000000"  // APDU 코드 키
    }
}