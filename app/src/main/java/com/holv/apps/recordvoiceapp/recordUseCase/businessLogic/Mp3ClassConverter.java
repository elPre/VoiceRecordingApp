package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Mp3ClassConverter implements Mp3Converter {

    @Override
    public File convertToMp3(InfoCovertToMp3 infoCovertToMp3) {
        final int thousendValue = 1000;
        File mp3File = null;
        File pathToDoc = infoCovertToMp3.getApp().getExternalFilesDir(Environment.DIRECTORY_MUSIC);

        String filePath = pathToDoc.getPath() + "/audiorecord.m4a";
        String outPutFile = pathToDoc.getPath() + "/"+infoCovertToMp3.getFileName();
        String quality = String.valueOf(infoCovertToMp3.getRecordType().getBiteRate() / thousendValue);

        StringBuilder sb = new StringBuilder("-i ")
                .append(filePath)
                .append(" -c:v copy -c:a libmp3lame -ab ")//need to pass the number through parameter
                .append(quality).append("k").append(" ")
                .append(outPutFile);

        int rc = FFmpeg.execute(sb.toString());

        if (rc == RETURN_CODE_SUCCESS) {
            Log.d("Mp3ClassConverter", "File converted to MP3");
            mp3File = new File(outPutFile);
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.d("Mp3ClassConverter", "Command execution cancelled by user.");
            //alert the user the recording could not
        } else {
            Log.d("Mp3ClassConverter", String.format("Command execution failed with rc=%d and the output below.", rc));
            //Config.printLastCommandOutput(Log.INFO);
        }
        return mp3File;
    }
}
