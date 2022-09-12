package com.holv.apps.recordvoiceapp.recordUseCase.businessLogic;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.os.Environment;
import android.util.Log;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

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

        int rc = -1;
        if (infoCovertToMp3.getRecordType().name().contains("MP3")) {
            rc = FFmpeg.execute(sb.toString());
        } else {
            String fileName = changeFileExtension(infoCovertToMp3);
            mp3File = renameFileNameAndDeleteItself(pathToDoc, fileName);
        }


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

    private String changeFileExtension(InfoCovertToMp3 info) {
        String fileName = info.getFileName();
        if (!info.getRecordType().name().contains("MP3")){
            fileName = fileName.replace(".mp3", ".m4a");
        }
        return fileName;
    }

    private File renameFileNameAndDeleteItself(File pathToDoc, String fileName) {
        File destination = null;
        try{
            File source = new File(pathToDoc.getPath() + "/audiorecord.m4a");
            destination = new File(pathToDoc.getPath() + "/"+ fileName);
            FileChannel sourceChannel = new FileInputStream(source).getChannel();
            FileChannel destChannel = new FileOutputStream(destination).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            source.delete();
        } catch (Exception e) {
            Log.e("Mp3ClassConverter","Could not rename the file " + e.getMessage());
        }
        return destination;
    }

}
