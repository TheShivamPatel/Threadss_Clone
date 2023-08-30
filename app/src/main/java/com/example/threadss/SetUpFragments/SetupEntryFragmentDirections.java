package com.example.threadss.SetUpFragments;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.threadss.R;

public class SetupEntryFragmentDirections {
  private SetupEntryFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionSetupEntryFragmentToLoginFragment() {
    return new ActionOnlyNavDirections(R.id.action_setupEntryFragment_to_loginFragment);
  }

  @NonNull
  public static NavDirections actionSetupEntryFragmentToRegistrationFragment() {
    return new ActionOnlyNavDirections(R.id.action_setupEntryFragment_to_registrationFragment);
  }
}
