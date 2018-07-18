package com.ubtechinc.codemaosdk.functions;

import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtech.utilcode.utils.notification.Subscriber;
import com.ubtechinc.alpha.CodeMaoRecogniseObject;
import com.ubtechinc.codemao.CodeMaoControlFindFace;
import com.ubtechinc.codemao.CodeMaoControlRegisterFace;
import com.ubtechinc.codemao.CodeMaoFaceAnalyze;
import com.ubtechinc.codemao.CodeMaoFaceDetect;
import com.ubtechinc.codemao.CodeMaoFaceInfo;
import com.ubtechinc.codemao.CodeMaoFaceRecognise;
import com.ubtechinc.codemao.CodeMaoGetRegisterFaces;
import com.ubtechinc.codemao.CodeMaoTakePicture;
import com.ubtechinc.codemaosdk.HandlerUtils;
import com.ubtechinc.codemaosdk.Phone2RobotMsgMgr;
import com.ubtechinc.codemaosdk.Statics;
import com.ubtechinc.codemaosdk.bean.Face;
import com.ubtechinc.codemaosdk.event.FindFaceEvent;
import com.ubtechinc.codemaosdk.interfaces.IVision;
import com.ubtechinc.protocollibrary.communite.ICallback;
import com.ubtechinc.protocollibrary.communite.ThrowableWrapper;
import com.ubtechinc.protocollibrary.protocol.CmdId;

import java.util.ArrayList;
import java.util.List;

/**
* @Description 视觉识别功能
* @Author tanghongyu
* @Time  2018/4/2 16:25
*/
public class VisionApi {

    private VisionApi() {}
    private static VisionApi visionApi = new VisionApi();
    public static VisionApi get() {
        if(visionApi == null) {
            synchronized (VisionApi.class) {
                visionApi = new VisionApi();
            }

        }
        return visionApi;
    }

    private IVision.FindFaceCallback findFaceCallback;
    public void recogniseFaceCount(int timeout,final IVision.GetFaceCountCallback getFaceCountCallback) {
        CodeMaoFaceDetect.FaceDetectRequest.Builder builder =CodeMaoFaceDetect.FaceDetectRequest.newBuilder();
        builder.setTimeout(timeout);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_GET_RECOGNISE_FACE_COUNT_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback<CodeMaoFaceDetect.FaceDetectResponse>() {
            @Override
            public void onSuccess(final CodeMaoFaceDetect.FaceDetectResponse data) {

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            getFaceCountCallback.onGetFaceCount(data.getCount());
                        }else {
                            getFaceCountCallback.onFail(data.getResultCode());
                        }

                    }
                });
            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                            getFaceCountCallback.onFail(e.getErrorCode());

                    }
                });
            }
        });
    }

    public void faceAnalyze(int timeout, final IVision.GetFaceAnalyzeCallback getFaceAnalyzeCallback) {
        CodeMaoFaceAnalyze.FaceAnalyzeRequest.Builder builder = CodeMaoFaceAnalyze.FaceAnalyzeRequest.newBuilder();
        builder.setTimeout(timeout);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_GET_FACE_ANALYZE_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback<CodeMaoFaceAnalyze.FaceAnalyzeResponse>() {
            @Override
            public void onSuccess(final CodeMaoFaceAnalyze.FaceAnalyzeResponse data) {

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {

                            List<Face> faceList = new ArrayList<>();
                            for(CodeMaoFaceInfo.FaceInfoResponse faceInfoResponse : data.getFaceInfosList()) {
                                Face face = new Face();
                                face.setAge(faceInfoResponse.getAge());
                                face.setGender(faceInfoResponse.getGender());
                                face.setWidth(faceInfoResponse.getWidth());
                                face.setHeight(faceInfoResponse.getHeight());
                                faceList.add(face);
                            }
                            getFaceAnalyzeCallback.onGetFaceFeature(faceList);

                        }else {
                            getFaceAnalyzeCallback.onFail(data.getResultCode());
                        }
                    }
                });
            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        getFaceAnalyzeCallback.onFail(e.getErrorCode());

                    }
                });
            }
        });
    }

    public void recogniseObject(int timeout, int objectType, final IVision.GetObjectCallback getObjectCallback) {
        CodeMaoRecogniseObject.RecogniseObjectRequest.Builder builder =  CodeMaoRecogniseObject.RecogniseObjectRequest.newBuilder();
        builder.setObjectType(objectType);
        builder.setTimeout(timeout);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_GET_RECOGNISE_OBJECT_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoRecogniseObject.RecogniseObjectResponse>() {
            @Override
            public void onSuccess(final CodeMaoRecogniseObject.RecogniseObjectResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            getObjectCallback.onGetObject(data.getObjectsList());

                        }else {
                            getObjectCallback.onFail(data.getResultCode());
                        }
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        getObjectCallback.onFail(e.getErrorCode());

                    }
                });
            }
        });
    }
    public void takePicImmediately(IVision.ControlCallback controlCallback) {
        takePicture(Statics.TAKE_PIC_IMMEDIATELY, controlCallback);
    }
    public void takePicWithFace() {
        takePicture(Statics.TAKE_PIC_WITH_FACE_DETECT, null);
    }
    private void takePicture( int type, final IVision.ControlCallback controlCallback) {
        CodeMaoTakePicture.TakePictureRequest.Builder builder =  CodeMaoTakePicture.TakePictureRequest.newBuilder();
        builder.setType(type);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_TAKE_PICTURE_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoTakePicture.TakePictureResponse>() {
            @Override
            public void onSuccess(final CodeMaoTakePicture.TakePictureResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {

                        if(controlCallback != null) {
                            if(data.getIsSuccess()) {
                                controlCallback.onSuccess();

                            }else {
                                controlCallback.onFail(data.getCode());
                            }
                        }

                    }
                });
            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(controlCallback != null) {
                            controlCallback.onFail(e.getErrorCode());
                        }


                    }
                });
            }
        });
    }
    public void recogniseFace(int timeout, final IVision.RecogniseFaceCallback recogniseFaceCallback) {
        CodeMaoFaceRecognise.FaceRecogniseRequest.Builder builder = CodeMaoFaceRecognise.FaceRecogniseRequest.newBuilder();
        builder.setTimeout(timeout);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_FACE_RECOGNISE_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoFaceRecognise.FaceRecogniseResponse>() {
            @Override
            public void onSuccess(final CodeMaoFaceRecognise.FaceRecogniseResponse data) {

                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        if(data.getIsSuccess()) {
                            List<Face> faceList = new ArrayList<>();
                            for(CodeMaoFaceInfo.FaceInfoResponse faceInfoResponse : data.getFaceInfosList()) {
                                Face face = new Face();
                                face.setId(faceInfoResponse.getId());
                                face.setName(faceInfoResponse.getName());
                                faceList.add(face);
                            }
                            recogniseFaceCallback.onGetFaceInfo(faceList);

                        }else {
                            recogniseFaceCallback.onFail(data.getResultCode());
                        }
                    }
                });

            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        recogniseFaceCallback.onFail(e.getErrorCode());

                    }
                });
            }
        });
    }

    public void findFace(int timeout, final IVision.FindFaceCallback findFaceCallback) {
        this.findFaceCallback = findFaceCallback;
        controlFindFace(timeout, Statics.FIND_FACE_STATUS_START);
        NotificationCenter.defaultCenter().subscriber(FindFaceEvent.class, faceEventSubscriber);
    }

    public void pauseFindFace() {

        controlFindFace(0, Statics.FIND_FACE_STATUS_PAUSE);
    }

    public void stopFindFace() {

        controlFindFace(0, Statics.FIND_FACE_STATUS_STOP);
    }

    private void controlFindFace(int timeout, int type) {
        CodeMaoControlFindFace.ControlFindFaceRequest.Builder builder = CodeMaoControlFindFace.ControlFindFaceRequest.newBuilder();
        builder.setTimeout(timeout);
        builder.setType(type);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_FIND_FACE_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoControlFindFace.ControlFindFaceResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlFindFace.ControlFindFaceResponse data) {


            }

            @Override
            public void onError(final ThrowableWrapper e) {

            }
        });

    }

    public void startRegisterFace(String name, final IVision.RegisterCallback callback) {
        CodeMaoControlRegisterFace.ControlRegisterFaceRequest.Builder builder =  CodeMaoControlRegisterFace.ControlRegisterFaceRequest.newBuilder();
        builder.setType(Statics.FACE_REGISTER_START);
        builder.setUserName(name);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_REGISTER_FACE_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoControlRegisterFace.ControlRegisterFaceResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlRegisterFace.ControlRegisterFaceResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {

                        if(data.getIsSuccess()) {
                            callback.onSuccess(data.getRegisterName());
                        }else {
                            callback.onFail(data.getResultCode());
                        }

                    }
                });


            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(e.getErrorCode());

                    }
                });
            }
        });

    }
    public void getRegisterFaces (final IVision.GetRegisterFacesCallback callback) {
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_GET_REGISTER_FACES_REQUEST, Statics.PROTO_VERSION, null, "", new ICallback< CodeMaoGetRegisterFaces.GetRegisterFacesResponse>() {
            @Override
            public void onSuccess(final  CodeMaoGetRegisterFaces.GetRegisterFacesResponse data) {
                final List<Face> faceList = new ArrayList<>();
                for(CodeMaoFaceInfo.FaceInfoResponse cmFaceInfoResponse: data.getFaceInfosList()) {

                    Face face = new Face();
                    face.setId(cmFaceInfoResponse.getId());
                    face.setName(cmFaceInfoResponse.getName());
                    face.setGender(cmFaceInfoResponse.getGender());
                    face.setAge(cmFaceInfoResponse.getAge());
                    faceList.add(face);
                }
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {

                        if(data.getIsSuccess()) {
                            callback.onGetFaces(faceList);
                        }else {
                            callback.onFail(data.getResultCode());
                        }

                    }
                });


            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(e.getErrorCode());

                    }
                });
            }
        });

    }
    public void stopRegisterFace( final IVision.ControlCallback callback) {
        CodeMaoControlRegisterFace.ControlRegisterFaceRequest.Builder builder = CodeMaoControlRegisterFace.ControlRegisterFaceRequest.newBuilder();
        builder.setType(Statics.FACE_REGISTER_STOP);
        Phone2RobotMsgMgr.get().sendData(CmdId.BL_CONTROL_REGISTER_FACE_REQUEST, Statics.PROTO_VERSION, builder.build(), "", new ICallback<CodeMaoControlRegisterFace.ControlRegisterFaceResponse>() {
            @Override
            public void onSuccess(final CodeMaoControlRegisterFace.ControlRegisterFaceResponse data) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {

                        if(data.getIsSuccess()) {
                            callback.onSuccess();
                        }else {
                            callback.onFail(data.getResultCode());
                        }

                    }
                });


            }

            @Override
            public void onError(final ThrowableWrapper e) {
                HandlerUtils.runUITask(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFail(e.getErrorCode());

                    }
                });
            }
        });

    }

    Subscriber<FindFaceEvent> faceEventSubscriber = new Subscriber<FindFaceEvent>() {
        @Override
        public void onEvent(FindFaceEvent findFaceEvent) {
            if(findFaceCallback != null) {

                switch (findFaceEvent.status) {
                    case Statics.FIND_FACE_STATUS_START:
                        findFaceCallback.onStart();
                        break;
                    case Statics.FIND_FACE_STATUS_CHANGE:
                        findFaceCallback.onFaceChange(findFaceEvent.faceCount);
                        break;
                    case Statics.FIND_FACE_STATUS_FAIL:
                        findFaceCallback.onFail(findFaceEvent.code);
                        break;
                    case Statics.FIND_FACE_STATUS_PAUSE:
                        findFaceCallback.onPause();
                        break;
                    case Statics.FIND_FACE_STATUS_STOP:
                        findFaceCallback.onStop();
                        break;
                }

            }

        }
    };

}
