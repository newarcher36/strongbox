package org.carlspring.strongbox.security.authentication.suppliers;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.carlspring.strongbox.authentication.api.impl.xml.SecurityTokenAuthentication;
import org.carlspring.strongbox.configuration.ConfigurationManager;
import org.carlspring.strongbox.storage.Storage;
import org.carlspring.strongbox.storage.repository.Repository;
import org.carlspring.strongbox.storage.repository.RepositoryLayoutEnum;
import org.carlspring.strongbox.users.security.SecurityTokenProvider;
import org.carlspring.strongbox.utils.UrlUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Bespalov
 *
 */
@Component
public class NugetApiKeyAuthenticationSupplier implements AuthenticationSupplier
{

    @Inject
    private SecurityTokenProvider securityTokenProvider;

    @Inject
    private ConfigurationManager configurationManager;

    @Override
    public Authentication supply(HttpServletRequest request)
    {
        final String nugetApiKey = request.getHeader("x-nuget-apikey");
        if (nugetApiKey == null)
        {
            return null;
        }
        String userName = securityTokenProvider.getSubject(nugetApiKey);
        String securityToken = (String) securityTokenProvider.getClaims(nugetApiKey)
                                                             .getClaimValue("security-token-key");
        return new SecurityTokenAuthentication(userName, securityToken);
    }

    @Override
    public boolean supports(HttpServletRequest request)
    {
        String storageId = UrlUtils.getCurrentStorageId();
        String repositoryId = UrlUtils.getCurrentRepositoryId();
        if (storageId == null || repositoryId == null)
        {
            return false;
        }

        Storage storage = configurationManager.getConfiguration()
                                              .getStorage(storageId);
        if (storage == null)
        {
            return false;
        }
        Repository repository = storage.getRepository(repositoryId);
        if (repository == null)
        {
            return false;
        }

        return RepositoryLayoutEnum.NUGET_HIERACHLICAL.getLayout().equals(repository.getLayout());
    }

}