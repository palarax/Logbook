package palarax.com.logbook.presenter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.backendless.Backendless;

import palarax.com.logbook.R;
import palarax.com.logbook.model.Users;
import palarax.com.logbook.model.Utils;

/**
 * Created by Ithai on 29/09/2017.
 */

public class UserPresenter {

    private Users mStudent;

    public UserPresenter(){
        this.mStudent = new Select().from(Users.class).where("licenseNumber = ?",
                (Integer) Backendless.UserService.CurrentUser().getProperty(Utils.BACKENDLESS_LICENSE)).executeSingle();
    }

    public Users getStudent(){
        return mStudent;
    }

    public void deleteAllUsers(){
        new Delete().from(Users.class).execute();
    }


    /**
     * Populates view with user data
     */
    public void populateUserData(TextView nameText, TextView licenseText,TextView dobText,
                                  TextView stateText, TextView progressText,Context context) {
        nameText.setText(mStudent.getUserName()
                + " " + mStudent.getUserSurname());
        licenseText.setText(mStudent.getLicenseNumber().toString());
        dobText.setText(mStudent.getDob());
        stateText.setText(mStudent.getState());

        if (mStudent.getHoursCompleted() >= 120) {
            progressText.setText(context.getString(R.string.profile_in_progress));
            progressText.setTextColor(ContextCompat.getColor(context, R.color.in_progress));
        }else{
            progressText.setText(context.getString(R.string.profile_completed));
            progressText.setTextColor(ContextCompat.getColor(context, R.color.licence_color));
        }
    }

}
