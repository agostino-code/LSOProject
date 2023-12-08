package com.example.guesstheword.view.login;

import android.content.Intent;
import android.widget.Toast;
import com.example.guesstheword.R;
import com.example.guesstheword.data.model.User;
import com.example.guesstheword.service.SocketService;
import com.example.guesstheword.view.BoundServiceActivity;
import com.example.guesstheword.view.menu.MenuActivity;
import org.json.JSONException;

public class LoginParentActivity extends BoundServiceActivity {
    @Override
    protected Class<?> getServiceClass() {
        return SocketService.class;
    }

    protected void updateUiWithUser(User user) {
        Intent switchActivities = new Intent(this, MenuActivity.class);
//        try {
//            switchActivities.putExtra("jsonUser", user.toJSONObject().toString());
//        } catch (JSONException e) {
//            showRegistrationFailed(getString(R.string.registration_failed));
//        }
//        String welcome = getString(R.string.welcome) + " " + user.getUsername() + "!";
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        startActivity(switchActivities);
    }
}
