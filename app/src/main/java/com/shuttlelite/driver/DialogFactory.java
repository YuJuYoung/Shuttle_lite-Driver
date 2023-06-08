package com.shuttlelite.driver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class DialogFactory {

    private static final MyAppInfo myAppInfo = MyAppInfo.getInstance();
    private static final ShuttleStatus shuttleStatus = myAppInfo.getShuttleStatus();

    public static void showNFCDialog(Context context) {
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(context);

        DialogBuilder
                .setTitle("NFC가 비활성화됨")
                .setMessage("NFC를 활성화 해야합니다.")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                });
        AlertDialog dialog = DialogBuilder.create();
        dialog.show();
    }

    public static void showGPSDialog(Context context) {
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(context);

        DialogBuilder
                .setTitle("GPS가 비활성화됨")
                .setMessage("GPS를 활성화 해야합니다.")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        AlertDialog dialog = DialogBuilder.create();
        dialog.show();
    }

    public static AlertDialog.Builder getFinishDialogBuilder(Context context) {
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(context);

        String title, message;

        if (shuttleStatus.getStatus() == ShuttleStatus.GOING_HOME) {
            title = "운행 종료";
            message = "운행을 종료 하시겠습니까?";
        } else {
            title = "탑승자 하차";
            message = "탑승자들을 하차 시키겠습니까?";
        }

        return DialogBuilder
                .setTitle(title)
                .setMessage(message);
    }

    public static void showStartDialog(Context context, int btnId, Intent newIntent) {
        AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(context);

        String title = btnId == R.id.go_house_btn ? "하원 운행" : "등원 운행";
        String message = "운행을 시작 시키겠습니까?";

        DialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (btnId == R.id.go_house_btn) {
                            shuttleStatus.setStatus(ShuttleStatus.GOING_HOME);
                        } else {
                            shuttleStatus.setStatus(ShuttleStatus.GOING_SCHOOL);
                        }
                        context.startActivity(newIntent);
                    }
                })
                .create()
                .show();
    }

}
