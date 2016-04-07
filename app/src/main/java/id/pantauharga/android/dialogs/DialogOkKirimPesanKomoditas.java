package id.pantauharga.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import id.pantauharga.android.R;
import id.pantauharga.android.aktivitas.PesanKomoditas;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class DialogOkKirimPesanKomoditas extends DialogFragment {

    private PesanKomoditas mLaporHargaAkt;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mLaporHargaAkt = (PesanKomoditas) DialogOkKirimPesanKomoditas.this.getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogOkKirimPesanKomoditas.this.getActivity());
        builder.setMessage(R.string.lapor_okkirimdatapesan);
        builder.setPositiveButton(R.string.teks_dialog_selesai, listenerok);
        builder.setNegativeButton(R.string.teks_dialog_kirim, listenerkirimlain);

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }


    DialogInterface.OnClickListener listenerok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            mLaporHargaAkt.setOkTerkirim();
            DialogOkKirimPesanKomoditas.this.dismiss();
        }
    };

    DialogInterface.OnClickListener listenerkirimlain = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            DialogOkKirimPesanKomoditas.this.dismiss();
        }
    };

}
