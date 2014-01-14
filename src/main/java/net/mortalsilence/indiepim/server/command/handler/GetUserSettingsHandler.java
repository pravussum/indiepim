package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.UserConfigKey;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetUserSettings;
import net.mortalsilence.indiepim.server.command.results.UserSettingsResult;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class GetUserSettingsHandler implements Command<GetUserSettings, UserSettingsResult> {

    @Inject
    private ConfigDAO configDAO;

	@Transactional (readOnly = true)
    @Override
    public UserSettingsResult execute(GetUserSettings action) {
	
		UserSettingsResult result = new UserSettingsResult();
        final Long userId = ActionUtils.getUserId();
        result.setGoogleAuthClientId(configDAO.getUserPropertyValue(userId, UserConfigKey.GOOGLE_AUTH_CLIENT_ID));
		result.setGoogleAuthClientSecret(configDAO.getUserPropertyValue(userId, UserConfigKey.GOOGLE_AUTH_CLIENT_SECRET));
		return result;
	}

	@Override
	public void rollback(GetUserSettings arg0, UserSettingsResult arg1) {
		// no use rolling back a getter
	}

}
