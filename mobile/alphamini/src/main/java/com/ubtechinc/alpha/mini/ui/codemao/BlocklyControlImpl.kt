package com.ubtechinc.alpha.mini.ui.codemao

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.text.TextUtils

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Lists
import com.ubtech.utilcode.utils.FileUtils
import com.ubtech.utilcode.utils.JsonUtils
import com.ubtech.utilcode.utils.LogUtils
import com.ubtech.utilcode.utils.Utils
import com.ubtechinc.alpha.mini.R
import com.ubtechinc.alpha.mini.common.DeviceDirectionEnum
import com.ubtechinc.alpha.mini.common.DirectionSensorEventListener
import com.ubtechinc.alpha.mini.common.IBlocklyControl
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant
import com.ubtechinc.alpha.mini.constants.Constants.PERMISSION_CODE_CODING
import com.ubtechinc.alpha.mini.entity.ControlBehavior
import com.ubtechinc.alpha.mini.entity.ControlRegisterFace
import com.ubtechinc.alpha.mini.entity.FindFace
import com.ubtechinc.alpha.mini.entity.MouthLamp
import com.ubtechinc.alpha.mini.entity.MoveRobot
import com.ubtechinc.alpha.mini.entity.ObjectDetect
import com.ubtechinc.alpha.mini.entity.Product
import com.ubtechinc.alpha.mini.entity.RegisterFace
import com.ubtechinc.alpha.mini.entity.ReportData
import com.ubtechinc.alpha.mini.entity.observable.AuthLive
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive
import com.ubtechinc.alpha.mini.repository.CodingOpusRepository
import com.ubtechinc.alpha.mini.repository.datasource.ICodingOpusDataSource
import com.ubtechinc.alpha.mini.ui.codemao.listener.ICodeMaoPresenter
import com.ubtechinc.alpha.mini.utils.PermissionUtils
import com.ubtechinc.alpha.mini.widget.MaterialDialog
import com.ubtechinc.codemaosdk.HandlerUtils
import com.ubtechinc.codemaosdk.MiniApi
import com.ubtechinc.codemaosdk.Statics
import com.ubtechinc.codemaosdk.bean.Expression
import com.ubtechinc.codemaosdk.bean.Face
import com.ubtechinc.codemaosdk.bean.RobotAction
import com.ubtechinc.codemaosdk.functions.ActionApi
import com.ubtechinc.codemaosdk.functions.ExpressionApi
import com.ubtechinc.codemaosdk.functions.LampApi
import com.ubtechinc.codemaosdk.functions.SensorApi
import com.ubtechinc.codemaosdk.functions.SpeechApi
import com.ubtechinc.codemaosdk.functions.VisionApi
import com.ubtechinc.codemaosdk.interfaces.IAction
import com.ubtechinc.codemaosdk.interfaces.IExpression
import com.ubtechinc.codemaosdk.interfaces.ILamp
import com.ubtechinc.codemaosdk.interfaces.IRobotBleControl
import com.ubtechinc.codemaosdk.interfaces.ISpeech
import com.ubtechinc.codemaosdk.interfaces.IVision
import com.ubtechinc.nets.http.ThrowableWrapper
import com.ubtechinc.nets.utils.JsonUtil
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.HashMap

/**
 * @Deseription 前端接口方法
 * @Author tanghongyu
 * @Time 2018/5/17 20:38
 */

class BlocklyControlImpl(private val iCodeMaoPresenter: ICodeMaoPresenter) : IBlocklyControl, DirectionSensorEventListener.ActionListener {



    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    private var mDirection = DeviceDirectionEnum.NONE.type
    private var mListener: DirectionSensorEventListener? = null

    init {
        //        registerRobotSensors();
        initPhoneDirectionSensor()
    }

    private fun initPhoneDirectionSensor() {
        mSensorManager = Utils.getContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    override fun bluetoothManage() {
        if (PermissionUtils.checkPermission(PERMISSION_CODE_CODING)) {
            iCodeMaoPresenter.bluetoothManage()
        } else {
            requestPermission(PERMISSION_CODE_CODING)
        }
    }

    override fun getRobotConnectionState() {
        LogUtils.d("Thread = " + Thread.currentThread().name)
        val state = MiniApi.get().isRobotConnected
        HandlerUtils.runUITask {
            val jsonObject = JSONObject()
            try {
                jsonObject.put(CodeMaoConstant.KEY_IS_CONNECT, (if (state) Statics.BLE_CONNECTED else Statics.BLE_DISCONNECT).toInt())
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val data = jsonObject.toString()
            requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_ROBOT_STATUS, data)))
        }

    }


    override fun playTTS(text: String) {
        SpeechApi.get().playTTS(text, object : ISpeech.PlayTTSCallback {
            override fun onBegin() {

            }

            override fun onCompleted() {


                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_PLAY_TTS, data)))
            }

            override fun onError(code: Int) {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_PLAY_TTS, data, code)))
            }
        })
    }

    override fun stopTTS() {
        SpeechApi.get().stopTTS(object : ISpeech.ControlCallback {
            override fun onSuccess() {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_STOP_TTS, data)))

            }

            override fun onError(code: Int) {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_STOP_TTS, data, code)))
            }
        })
    }

    override fun getActionList() {
        ActionApi.get().getActionList(object : IAction.getActionListCallback {
            override fun onGetActionList(list: List<RobotAction>) {
                val data = JsonUtils.object2Json(list)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_ACTION_LIST, data)))
            }

            override fun onError(i: Int) {

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_ACTION_LIST, JsonUtils.object2Json(Lists.newArrayList<Any>()), i)))

            }
        })
    }

    override fun playAction(actionName: String) {
        ActionApi.get().playAction(actionName, object : IAction.playActionCallback {
            override fun onCompleted() {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_PLAY_ACTION, data)))
            }

            override fun onError(code: Int) {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_PLAY_ACTION, data, code)))
            }
        })
    }

    override fun stopAllAction() {
        ActionApi.get().stopAction(object : IAction.stopActionCallback {
            override fun onSuccess() {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_STOP_ACTION, data)))
            }

            override fun onError(code: Int) {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_STOP_ACTION, data, code)))

            }
        })
    }

    override fun faceDetect(timeout: Int) {
        VisionApi.get().recogniseFaceCount(timeout, object : IVision.GetFaceCountCallback {
            override fun onGetFaceCount(count: Int) {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_FACE_COUNT, count))
                LogUtils.d(data)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_FACE_DETECT, data)))


            }

            override fun onFail(code: Int) {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_FACE_COUNT, 0))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_FACE_DETECT, data, code)))
            }
        })
    }

    override fun faceAnalysis(timeout: Int) {
        VisionApi.get().faceAnalyze(timeout, object : IVision.GetFaceAnalyzeCallback {
            override fun onGetFaceFeature(faceList: List<Face>) {

                val data = JsonUtils.object2Json(faceList)
                LogUtils.d("onGetFaceFeature  = $data")

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_FACE_ANALYSIS, data)))
            }

            override fun onFail(code: Int) {

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_FACE_ANALYSIS, "", code)))
            }
        })
    }

    override fun objectDetect(content: String) {
        val objectDetect = JsonUtils.jsonToBean(content, ObjectDetect::class.java)
        VisionApi.get().recogniseObject(objectDetect.timeout, objectDetect.type, object : IVision.GetObjectCallback {
            override fun onGetObject(objects: List<String>) {


                val data = JsonUtils.object2Json(objects)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_OBJECT_DETECT, data)))
            }

            override fun onFail(code: Int) {

                val data = JsonUtils.object2Json(Lists.newArrayList<Any>())

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_OBJECT_DETECT, data, code)))
            }
        })
    }

    override fun faceRecognise(timeout: Int) {
        VisionApi.get().recogniseFace(timeout, object : IVision.RecogniseFaceCallback {
            override fun onGetFaceInfo(faceList: List<Face>) {
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_FACE_RECOGNISE, JsonUtils.object2Json(faceList))))
            }

            override fun onFail(code: Int) {
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_FACE_RECOGNISE, JsonUtils.object2Json(Lists.newArrayList<Any>()), code)))
            }
        })
    }

    override fun getExpressionList() {
        ExpressionApi.get().getExpressList(object : IExpression.GetExpressionList {
            override fun onGetExpressionList(list: List<Expression>) {


                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_EXPRESSION_LIST, JsonUtils.object2Json(list))))

            }

            override fun onError(code: Int) {

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK,
                        getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_EXPRESSION_LIST, JsonUtils.object2Json(Lists.newArrayList<Any>()), code)))
            }
        })
    }

    override fun playExpression(expression: String) {
        ExpressionApi.get().playExpression(expression, object : IExpression.ControlCallback {
            override fun onSuccess() {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_PLAY_EXPRESSION, data)))

            }

            override fun onError(code: Int) {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_PLAY_EXPRESSION, data, code)))
            }
        })
    }


    private fun registerRobotSensorsAndReport() {
        SensorApi.get().registerBatteryEvent { level, state ->
            val map = HashMap<String, Int>()
            map[CodeMaoConstant.KEY_LEVEL] = level
            map[CodeMaoConstant.KEY_STATUS] = state
            val data = JsonUtils.object2Json(map)

            requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_ROBOT_BATTERY, data)))
        }
        SensorApi.get().registerRobotPostureEvent { type ->
            val map = HashMap<String, Int>()
            map[CodeMaoConstant.KEY_STATUS] = type
            val data = JsonUtils.object2Json(map)

            requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_ROBOT_POSTURE, data)))
        }
        SensorApi.get().observeHeadRacketEvent { type ->
            val map = HashMap<String, Int>()
            map[CodeMaoConstant.KEY_TYPE] = type
            val data = JsonUtils.object2Json(map)

            requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_ROBOT_HEAD_RACKET, data)))
        }
        SensorApi.get().observeVolumeKeyEvent { type ->
            val map = HashMap<String, Int>()
            map[CodeMaoConstant.KEY_TYPE] = type
            val data = JsonUtils.object2Json(map)

            requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_ROBOT_VOLUME_KEY_PRESS, data)))
        }

        SensorApi.get().registerInfraredDistanceEvent(2000) { distance ->
            val map = HashMap<String, Int>()
            map[CodeMaoConstant.KEY_DISTANCE] = distance
            val data = JsonUtils.object2Json(map)

            requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_INFRARED_DISTANCE, data)))
        }


    }

    override fun controlMouthLamp(isOpen: Boolean) {

        LampApi.get().controlMouthLamp(isOpen, object : ILamp.ControlCallback {
            override fun onSuccess() {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SWITCH_MOUTH_LAMP, data)))


            }

            override fun onError(code: Int) {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SWITCH_MOUTH_LAMP, data, code)))
            }
        })

    }

    override fun setMouthLamp(content: String) {
        val mouthLamp = JsonUtils.jsonToBean(content, MouthLamp::class.java)

        if (mouthLamp.model == Statics.MOUTH_LAMP_MODLE_NORMAL.toInt()) {
            LampApi.get().startMouthNormalLamp(mouthLamp.color, mouthLamp.duration, object : ILamp.ControlCallback {
                override fun onSuccess() {

                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                    LogUtils.d(data)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SET_MOUTH_LAMP, data)))

                }

                override fun onError(code: Int) {
                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                    LogUtils.d(data)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SET_MOUTH_LAMP, data, code)))
                }
            })

        } else {
            LampApi.get().startMouthBreathLamp(mouthLamp.color, mouthLamp.breathDuration, mouthLamp.duration, object : ILamp.ControlCallback {
                override fun onSuccess() {

                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                    LogUtils.d(data)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SET_MOUTH_LAMP, data)))

                }

                override fun onError(code: Int) {
                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                    LogUtils.d(data)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SET_MOUTH_LAMP, data, code)))
                }
            })

        }


    }

    override fun takePicture(type: Int) {
        when (type) {
            Statics.TAKE_PIC_WITH_FACE_DETECT.toInt() -> VisionApi.get().takePicWithFace()

            Statics.TAKE_PIC_IMMEDIATELY.toInt() -> VisionApi.get().takePicImmediately(object : IVision.ControlCallback {
                override fun onSuccess() {


                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                    LogUtils.d(data)

                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_TAKE_PIC, data)))

                }

                override fun onFail(code: Int) {

                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                    LogUtils.d(data)

                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_TAKE_PIC, data, code)))
                }
            })
        }
    }

    private fun reportFindFaceStatus(status: Int, faceCount: Int) {
        val map = HashMap<String, Int>()
        map[CodeMaoConstant.KEY_STATUS] = status
        map[CodeMaoConstant.KEY_FACE_COUNT] = faceCount
        val data = JsonUtils.object2Json(map)

        requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_FIND_FACE_STATE, data)))

    }

    override fun controlFindFace(content: String) {
        val findFace = JsonUtils.jsonToBean(content, FindFace::class.java)

        when (findFace.type) {

            Statics.FIND_FACE_CONTROL_START -> VisionApi.get().findFace(findFace.timeout, object : IVision.FindFaceCallback {
                override fun onFaceChange(count: Int) {
                    reportFindFaceStatus(Statics.FIND_FACE_STATUS_CHANGE.toInt(), count)
                }

                override fun onStart() {
                    reportFindFaceStatus(Statics.FIND_FACE_STATUS_START.toInt(), 0)
                }

                override fun onPause() {
                    reportFindFaceStatus(Statics.FIND_FACE_STATUS_PAUSE.toInt(), 0)
                }

                override fun onStop() {
                    reportFindFaceStatus(Statics.FIND_FACE_STATUS_STOP.toInt(), 0)
                }

                override fun onFail(code: Int) {
                    reportFindFaceStatus(Statics.FIND_FACE_STATUS_FAIL.toInt(), 0)
                }
            })

            Statics.FIND_FACE_CONTROL_PAUSE -> VisionApi.get().pauseFindFace()

            Statics.FIND_FACE_CONTROL_STOP -> VisionApi.get().stopFindFace()
        }

    }

    override fun moveRobot(content: String) {
        val moveRobot = JsonUtils.jsonToBean(content, MoveRobot::class.java)
        LogUtils.d("move Robot = $content")
        ActionApi.get().moveRobotByCustom(moveRobot.direction.toByte(), moveRobot.step, object : IAction.playActionCallback {
            override fun onCompleted() {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_MOVE_ROBOT, data)))
            }

            override fun onError(code: Int) {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_MOVE_ROBOT, data, code)))
            }
        })
    }

    override fun switchTranslateModel(isEnter: Boolean) {
        SpeechApi.get().switchTranslateModel(isEnter, object : ISpeech.ControlCallback {
            override fun onSuccess() {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SWITCH_TRANSLATE, data)))
            }

            override fun onError(code: Int) {

                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                LogUtils.d(data)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_SWITCH_TRANSLATE, data, code)))

            }
        })
    }

    override fun controlRegisterFace(content: String) {
        val registerFace = JsonUtils.jsonToBean(content, RegisterFace::class.java)
        if (registerFace.isStart) {

            VisionApi.get().startRegisterFace(registerFace.name, object : IVision.RegisterCallback {
                override fun onSuccess(registerName: String) {
                    val controlRegisterFace = ControlRegisterFace()
                    controlRegisterFace.name = registerName
                    controlRegisterFace.isSuccess = true
                    val data = JsonUtils.object2Json(controlRegisterFace)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_REGISTER_FACE, data)))

                }

                override fun onFail(code: Int) {
                    val controlRegisterFace = ControlRegisterFace()
                    controlRegisterFace.isSuccess = false
                    val data = JsonUtils.object2Json(controlRegisterFace)
                    LogUtils.d(data)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_REGISTER_FACE, data, code)))
                }
            })

        } else {
            VisionApi.get().stopRegisterFace(object : IVision.ControlCallback {
                override fun onSuccess() {
                    val controlRegisterFace = ControlRegisterFace()
                    controlRegisterFace.isSuccess = true
                    val data = JsonUtils.object2Json(controlRegisterFace)
                    LogUtils.d(data)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_REGISTER_FACE, data)))
                }

                override fun onFail(code: Int) {
                    val controlRegisterFace = ControlRegisterFace()
                    controlRegisterFace.isSuccess = false
                    val data = JsonUtils.object2Json(controlRegisterFace)
                    LogUtils.d(data)
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_REGISTER_FACE, data, code)))
                }
            })
        }

    }

    override fun getRegisterFaces() {
        VisionApi.get().getRegisterFaces(object : IVision.GetRegisterFacesCallback {
            override fun onGetFaces(faceList: List<Face>) {
                val data = JsonUtils.object2Json(faceList)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_REGISTER_FACES, data)))

            }

            override fun onFail(code: Int) {
                val data = JsonUtils.object2Json(Lists.newArrayList<Any>())
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_REGISTER_FACES, data, code)))
            }
        })
    }

    override fun controlBehavior(content: String) {
        val controlBehavior = JsonUtils.jsonToBean(content, ControlBehavior::class.java)

        if (controlBehavior.isStart) {
            ExpressionApi.get().startBehavior(controlBehavior.name, object : IExpression.ControlCallback {
                override fun onSuccess() {
                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                    LogUtils.d(data)

                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_BEHAVIOR, data)))
                }

                override fun onError(code: Int) {
                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                    LogUtils.d(data)

                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_BEHAVIOR, data, code)))
                }
            })
        } else {
            ExpressionApi.get().stopBehavior(object : IExpression.ControlCallback {
                override fun onSuccess() {
                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                    LogUtils.d(data)

                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_BEHAVIOR, data)))
                }

                override fun onError(code: Int) {
                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                    LogUtils.d(data)

                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CONTROL_BEHAVIOR, data, code)))
                }
            })
        }
    }

    override fun loadProductList() {


        //        ProductReponsitory.getInstance().loadProductList(new IProductCallback.GetProductList() {
        //            @Override
        //            public void onLoadProductList(List<Product> productList) {
        //                String data = JsonUtils.object2Json(productList);
        //
        //                requestJS(String.format(CodeMaoConstant.INSTANCE.getJS_CALLBACK(), getReportDataStr(CodeMaoConstant.INSTANCE.getEVENT_TYPE_GET_PRODUCT_LIST(), data)));
        //            }
        //
        //            @Override
        //            public void onDataNotAvailable(ThrowableWrapper e) {
        //                requestJS(String.format(CodeMaoConstant.INSTANCE.getJS_CALLBACK(), getReportDataStr(CodeMaoConstant.INSTANCE.getEVENT_TYPE_GET_PRODUCT_LIST(), JsonUtil.object2Json(Lists.newArrayList()))));
        //            }
        //        });

        CodingOpusRepository.getAllOpus(AuthLive.getInstance().userId, object : ICodingOpusDataSource.GetOpusListCallback {
            override fun onLoadOpusList(productList: List<Product>) {
                val data = JsonUtils.object2Json(productList)

                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_PRODUCT_LIST, data)))
            }

            override fun onDataNotAvailable(e: ThrowableWrapper) {
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_PRODUCT_LIST, JsonUtil.object2Json(Lists.newArrayList<Any>()))))
            }
        })

    }
    class SaveProductData {
        var isSuccess = false
        var productId = ""
    }
    override fun saveProduct(content: String) {
        if (PermissionUtils.checkPermission(PERMISSION_CODE_CODING)) {
            LogUtils.d("saveProduct = $content")
            val productInfo = JsonUtils.jsonToBean(content, Product::class.java)
            productInfo.userId = AuthLive.getInstance().userId
            FileUtils.writeFileFromString(FileUtils.getSDCardPath() + "/test/text.txt", content, true)
            if (TextUtils.isEmpty(productInfo.id)) {
                val id = CodingOpusRepository.saveOpus(productInfo)
                var saveProductData = SaveProductData()
                saveProductData.isSuccess = true
                saveProductData.productId = id
                val data = JsonUtils.object2Json(saveProductData)
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_ADD_PRODUCT, data)))
            } else {
                CodingOpusRepository.modifyOpus(productInfo)
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_ADD_PRODUCT, data)))
            }
        } else {
            requestPermission(PERMISSION_CODE_CODING)
        }


        //        ProductReponsitory.getInstance().addProduct(productInfo.getContent(), productInfo.getName(), new IProductCallback.ControlProductCallback() {
        //            @Override
        //            public void onSuccess() {
        //                String data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.INSTANCE.getKEY_IS_SUCCESS(), true));
        //                LogUtils.d(data);
        //
        //                requestJS(String.format(CodeMaoConstant.INSTANCE.getJS_CALLBACK(), getReportDataStr(CodeMaoConstant.INSTANCE.getEVENT_TYPE_ADD_PRODUCT(), data)));
        //
        //            }
        //
        //            @Override
        //            public void onFail(ThrowableWrapper e) {
        //                String data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.INSTANCE.getKEY_IS_SUCCESS(), false));
        //                LogUtils.d(data);
        //
        //                requestJS(String.format(CodeMaoConstant.INSTANCE.getJS_CALLBACK(), getReportDataStr(CodeMaoConstant.INSTANCE.getEVENT_TYPE_ADD_PRODUCT(), data)));
        //            }
        //        });
    }

    override fun upload(content: String) {
        iCodeMaoPresenter.upload(content)
    }

    override fun deleteProduct(id: String) {

        CodingOpusRepository.deleteOpus(id)
        val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
        requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_DELETE_PRODUCT, data)))
        //        ProductReponsitory.getInstance().delProduct(id, new IProductCallback.ControlProductCallback() {
        //            @Override
        //            public void onSuccess() {
        //                String data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.INSTANCE.getKEY_IS_SUCCESS(), true));
        //                LogUtils.d(data);
        //
        //                requestJS(String.format(CodeMaoConstant.INSTANCE.getJS_CALLBACK(), getReportDataStr(CodeMaoConstant.INSTANCE.getEVENT_TYPE_DELETE_PRODUCT(), data)));
        //            }
        //
        //            @Override
        //            public void onFail(ThrowableWrapper e) {
        //                String data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.INSTANCE.getKEY_IS_SUCCESS(), false));
        //                LogUtils.d(data);
        //
        //                requestJS(String.format(CodeMaoConstant.INSTANCE.getJS_CALLBACK(), getReportDataStr(CodeMaoConstant.INSTANCE.getEVENT_TYPE_DELETE_PRODUCT(), data)));
        //            }
        //        });
    }

    override fun getProductContent(id: String) {
        requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_GET_PRODUCT_DETAIL, JsonUtils.object2Json(CodingOpusRepository.getOpusDetail(id)))))
    }

    override fun revertRobotOrigin() {
        MiniApi.get().revertOrigin(object : IRobotBleControl.ControlCallback {
            override fun onSuccess() {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REVERT_ROBOT_ORIGINAL, data)))
            }

            override fun onError(code: Int) {
                val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, false))
                requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REVERT_ROBOT_ORIGINAL, data, code)))
            }
        })
    }


    override fun exitCodingModel() {
        iCodeMaoPresenter.closeBlocklyWindow()
    }

    override fun reportPhoneKeyBack() {
        requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_PHONE_KEY_DOWN_BACK, "")))
    }
    override fun startRunProgram() {

    }
    override fun checkRunCondition() {
        if (PermissionUtils.checkPermission(PERMISSION_CODE_CODING)) {

            iCodeMaoPresenter.checkRunCondition(object : ICodeMaoPresenter.CheckRunConditionCallback {
                override fun onSuccess() {


                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_CAN_RUN, true))
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CHECK_RUN_CONDITION, data)))
                }

                override fun onFail() {
                    val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_CAN_RUN, false))
                    requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_CHECK_RUN_CONDITION, data)))
                }
            })
        } else {
            requestPermission(PERMISSION_CODE_CODING)
        }

    }

    override fun stopRunProgram() {
        iCodeMaoPresenter.stopRunProgram()
    }


    override fun unregisterRobotSensor() {
        SensorApi.get().unregisterBatteryEvent()
        SensorApi.get().unregisterFalldownEvent()
        SensorApi.get().unregisterHeadRacketEvent()
        SensorApi.get().unregisterVolumeKeyEvent()
        SensorApi.get().unregisterInfraredDistanceEvent()
    }

    override fun registerPhoneSensors() {
        if (mListener == null) {
            mListener = DirectionSensorEventListener(this)
            mSensorManager!!.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun unregisterPhoneSensor() {
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(mListener)
            mListener = null
        }
    }


    fun reportPhoneDirection() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(CodeMaoConstant.KEY_PHONE_DIRECTION, mDirection)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val data = jsonObject.toString()
        LogUtils.d("reportPhoneDirection data = $data")

        requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_PHONE_DIRECTION, data)))
    }

    private fun getReportDataStr(eventType: Int, dataContent: String): String {
        return getReportDataStr(eventType, dataContent, 0)
    }
    private fun getReportDataStr(eventType: Int, dataContent: String, resultCode : Int): String {
        val reportData = ReportData()
        reportData.dataContent = dataContent
        reportData.eventType = eventType
        reportData.resultCode = resultCode
        val data = JsonUtils.object2Json(reportData)
        LogUtils.d("getReportDataStr = $data")
        return data
    }

    private fun requestJS(js: String) {
        iCodeMaoPresenter.requestJS(js)
        LogUtils.d("requestJS = $js")
    }

    /**
     * 以下实现加速度传感器的监听状态的改变
     * onShake 左右摇晃, onLeftUp 向左倾斜， onRightUp 向右倾斜
     * onTopUp 向上倾斜，onBottomUp 向下倾斜，onDirectionNone None
     */

    override fun onShake(timeStamp: Long, value: FloatArray) {
        if (mDirection != DeviceDirectionEnum.SWING.type) {
            mDirection = DeviceDirectionEnum.SWING.type
            reportPhoneDirection()
        }

    }

    override fun onLeftUp() {
        if (mDirection != DeviceDirectionEnum.LEFT.type) {
            mDirection = DeviceDirectionEnum.LEFT.type
            reportPhoneDirection()
        }
    }

    override fun onRightUp() {
        if (mDirection != DeviceDirectionEnum.RIGHT.type) {
            mDirection = DeviceDirectionEnum.RIGHT.type
            reportPhoneDirection()
        }
    }

    override fun onTopUp() {
        if (mDirection != DeviceDirectionEnum.UP.type) {
            mDirection = DeviceDirectionEnum.UP.type
            reportPhoneDirection()
        }
    }

    override fun onBottomUp() {
        if (mDirection != DeviceDirectionEnum.DOWN.type) {
            mDirection = DeviceDirectionEnum.DOWN.type
            reportPhoneDirection()
        }
    }

    override fun onDirectionNone() {
        if (mDirection != DeviceDirectionEnum.NONE.type) {
            mDirection = DeviceDirectionEnum.NONE.type
            reportPhoneDirection()
        }
    }


    override fun registerRobotSensors() {
        registerRobotSensorsAndReport()
    }

    override fun reportRobotBleStatus(status: Boolean) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(CodeMaoConstant.KEY_IS_CONNECT, (if (status) Statics.BLE_CONNECTED else Statics.BLE_DISCONNECT).toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val data = jsonObject.toString()
        LogUtils.d("getRobotConnectionState data = $data")
        requestJS(String.format(CodeMaoConstant.JS_REPORT, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_ROBOT_STATUS, data)))
    }

    override fun startPlayVideo(url: String) {
        iCodeMaoPresenter.startPlayVideo(url)

        val data = JsonUtils.object2Json(ImmutableMap.of(CodeMaoConstant.KEY_IS_SUCCESS, true))
        LogUtils.d(data)
        requestJS(String.format(CodeMaoConstant.JS_CALLBACK, getReportDataStr(CodeMaoConstant.EVENT_TYPE_REPORT_VIDEO_PLAY_FINISH, data)))
    }

    private fun requestPermission(code: String) {
        val context = iCodeMaoPresenter.getContext()
        val materialDialog = MaterialDialog(context)
        val currentRobot = MyRobotsLive.getInstance().currentRobot.value
        val title = context.getString(R.string.permission_message_hint, context.getString(R.string.coding), currentRobot!!.masterUserName)
        materialDialog.setTitle(title)
        materialDialog.setNegativeButton(R.string.cancel) { materialDialog.dismiss() }
        materialDialog.setPositiveButton(R.string.confirm) {
            (context as CodeMaoActivity).runOnUiThread {
                PermissionUtils.requestPermission(context as LifecycleOwner, code)
                materialDialog.dismiss()
            }
        }
        materialDialog.show()
    }

    override fun startRender(url: String) {
        requestJS(String.format(CodeMaoConstant.JS_START_RENDER, url))
    }
}
