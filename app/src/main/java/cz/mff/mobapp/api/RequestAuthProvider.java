package cz.mff.mobapp.api;

import java.util.Map;

public interface RequestAuthProvider {

    Map<String, String> getAuthorizationHeaders();

}
