package com.fearmygaze.mApp.Controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fearmygaze.mApp.BuildConfig;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.database.UserDatabase;
import com.fearmygaze.mApp.interfaces.IUserStatus;
import com.fearmygaze.mApp.interfaces.IVolley;
import com.fearmygaze.mApp.interfaces.forms.IFormSignIn;
import com.fearmygaze.mApp.interfaces.forms.IFormSignUp;
import com.fearmygaze.mApp.interfaces.forms.IFormUpdate;
import com.fearmygaze.mApp.model.User;
import com.fearmygaze.mApp.model.User1;
import com.fearmygaze.mApp.util.NetworkConnection;
import com.fearmygaze.mApp.util.PrivatePreference;
import com.fearmygaze.mApp.util.RequestSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 *  TODO: Maybe add the device language to send back the correct error messages in the appropriate
 *   language
 *
 *  TODO: Construct the user preferences for the settings like {NetworkConnection class and more}
 */
public class UserController {
    public static UserDatabase database;

    public static void signUp(String username, String email, String password, String image, Context context, IFormSignUp iFormSignUp) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);
        body.put("image", image);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(0, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        iFormSignUp.onSuccess(message);
                        break;
                    case "404":
                    case "405":
                        iFormSignUp.onError(message);
                        break;
                    case "666":
                        iFormSignUp.onValidationError(error);
                        break;
                }

            } catch (JSONException e) {
                iFormSignUp.onError(context.getString(R.string.jsonError));
            }
        }, error -> iFormSignUp.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iFormSignUp.onError(context.getString(R.string.networkError));
        }
    }

    public static void signIn(String credential, String password, Context context, IFormSignIn iFormSignIn) {
        Map<String, String> body = new HashMap<>();
        body.put("loginCredential", credential);
        body.put("password", password);
        database = UserDatabase.getInstance(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(1, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        int id = response.getJSONObject("data").getInt("id");
                        String username = response.getJSONObject("data").getString("username");
                        String email = response.getJSONObject("data").getString("email");
                        String image = response.getJSONObject("data").getString("image");

//                        User user = new User(id, username, BuildConfig.PROFILE + image, email);

                        database.userDao().insertUser(new User1(id, username, BuildConfig.PROFILE + image ,email));
//
//                        PrivatePreference preference = new PrivatePreference(context);
//
//                        preference.putInt("id", id);
//                        preference.putString("username", username);
//                        preference.putString("image", BuildConfig.PROFILE + image);
//                        preference.putString("email", email);

                        iFormSignIn.onSuccess(id,message);
                        break;
                    case "404":
                    case "405":
                        iFormSignIn.onError(message);
                        break;
                    case "666":
                        iFormSignIn.onValidationError(message);
                        break;
                }

            } catch (JSONException e) {
                iFormSignIn.onError(context.getString(R.string.jsonError));
            }
        }, error -> iFormSignIn.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iFormSignIn.onError(context.getString(R.string.networkError));
        }
    }

    public static void updatePassword(int id, String newPassword, String oldPassword, Context context, IFormUpdate iUpdate) {
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);
        body.put("newPassword", newPassword);
        body.put("oldPassword", oldPassword);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(2, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        iUpdate.onSuccess(null, message);
                        break;
                    case "404":
                    case "405":
                        iUpdate.onError(message);
                        break;
                    case "666":
                        iUpdate.onValidationError(message);
                        break;
                }

            } catch (JSONException e) {
                iUpdate.onError(context.getString(R.string.jsonError));
            }
        }, error -> iUpdate.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iUpdate.onError(context.getString(R.string.networkError));
        }
    }

    public static void updateImage(User user,String image, Context context, IFormUpdate iUpdate) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", user.getId());
        body.put("username",user.getUsername());
        body.put("image", image);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(3, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        String mImage = response.getString("data");

                        User mUser = new User(user.getId(), user.getUsername(), BuildConfig.PROFILE + mImage, user.getEmail());

                        PrivatePreference pf = new PrivatePreference(context);
                        pf.putInt("id", user.getId());
                        pf.putString("username", user.getUsername());
                        pf.putString("image", BuildConfig.PROFILE + mImage);
                        pf.putString("email", user.getEmail());

                        iUpdate.onSuccess(mUser, message);
                        break;
                    case "404":
                    case "405":
                        iUpdate.onError(message);
                        break;
                    case "666":
                        iUpdate.onValidationError(message);
                        break;
                }

            } catch (JSONException e) {
                iUpdate.onError(context.getString(R.string.jsonError));
            }
        }, error -> iUpdate.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iUpdate.onError(context.getString(R.string.networkError));
        }
    }

    public static void statusCheck(int id, Context context, IUserStatus iUserStatus) {
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(4, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        int _id = response.getJSONObject("data").getInt("id");
                        String username = response.getJSONObject("data").getString("username");
                        String image = response.getJSONObject("data").getString("image");
                        String email = response.getJSONObject("data").getString("email");

//                        PrivatePreference preference = new PrivatePreference(context);
//                        preference.putInt("id", id);
//                        preference.putString("username", username);
//                        preference.putString("image", BuildConfig.PROFILE + image);
//                        preference.putString("email", email);

                        User1 user = new User1(_id, username, BuildConfig.PROFILE + image, email);

                        iUserStatus.onSuccess(user);
                        break;
                    case "404":
                    case "405":
                        iUserStatus.onExit(message);
                        break;
                }

            } catch (JSONException e) {
                iUserStatus.onError(context.getString(R.string.jsonError));
            }
        }, error -> iUserStatus.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iUserStatus.onError(context.getString(R.string.networkError));
        }
    }

    public static void delete(int id, Context context, IVolley iVolley) {//TODO: This will be inside the settings activity
        Map<String, Object> body = new HashMap<>();
        body.put("userID", id);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(5, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        iVolley.onSuccess(message);
                        break;
                    case "404":
                    case "405":
                        iVolley.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iVolley.onError(context.getString(R.string.jsonError));
            }
        }, error -> iVolley.onError(context.getString(R.string.volleyError))) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iVolley.onError(context.getString(R.string.networkError));
        }
    }

    /*
    * TODO: This will change with more options and stuff
    * */

    public static void report(int id, int reportedUserID, String reason, String description, Context context, IVolley iVolley) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("reportedUserID", reportedUserID);
        body.put("reason", reason);
        body.put("description", description);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url(6, context), new JSONObject(body), response -> {
            try {
                String message = response.getString("message");
                String error = response.getString("code");

                switch (error) {
                    case "200":
                        iVolley.onSuccess(message);
                        break;
                    case "404":
                    case "405":
                        iVolley.onError(message);
                        break;
                }

            } catch (JSONException e) {
                iVolley.onError(context.getString(R.string.jsonError));
            }
        }, error -> iVolley.onError(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        if (NetworkConnection.isConnectionAlive(context)) {
            RequestSingleton.getInstance(context).addToRequestQueue(request);
        } else {
            iVolley.onError(context.getString(R.string.networkError));
        }
    }

    private static String url(int pos, Context con) {
        String server = BuildConfig.SERVER;
        String[] url = con.getResources().getStringArray(R.array.user);
        return server + url[pos];
    }

}