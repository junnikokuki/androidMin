package com.ubtechinc.codemaosdk.interfaces;

import com.ubtechinc.codemaosdk.bean.Face;

import java.util.List;

/**
 * Created by tanghongyu on 2018/3/12.
 */

public interface IVision {

    public interface GetFaceCountCallback {

            void onGetFaceCount(int count);
            void onFail(int code);
    }
    public interface GetFaceAnalyzeCallback {
        void onGetFaceFeature(List<Face> faceList);
        void onFail(int code);
    }

    public interface GetObjectCallback {
        void onGetObject(List<String> objects);
        void onFail(int code);
    }

    public interface FindFaceCallback {
        void onFaceChange(int count);
        void onStart();
        void onPause();
        void onStop();
        void onFail(int code);
    }
    public interface RecogniseFaceCallback {
        void onGetFaceInfo(List<Face> faceList);
        void onFail(int code);
    }

    public interface GetRegisterFacesCallback {
        void onGetFaces(List<Face> faceList);
        void onFail(int code);
    }

    public interface RegisterCallback{
        void onSuccess(String registerName);
        void onFail(int code);
    }
    public interface ControlCallback{
        void onSuccess();
        void onFail(int code);
    }


}
