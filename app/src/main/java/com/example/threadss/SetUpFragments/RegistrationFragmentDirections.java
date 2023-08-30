package com.example.threadss.SetUpFragments;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.threadss.R;

public class RegistrationFragmentDirections {
  private RegistrationFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionRegistrationFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_registrationFragment_to_loginFragment);
  }
}
