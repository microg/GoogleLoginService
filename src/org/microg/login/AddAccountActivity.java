/*
 * Copyright (c) 2014 μg Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.microg.login;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.gsf.login.AndroidManager;
import com.google.auth.AuthType;

public class AddAccountActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getAction().equals("org.microg.login.ADD_ACCOUNT")) {
            String email = getIntent().getStringExtra("email");
            String token = getIntent().getStringExtra("masterToken");
            new AndroidManager(this).addAccount(email, token, AuthType.MasterToken);
        }
        finish();
    }
}
