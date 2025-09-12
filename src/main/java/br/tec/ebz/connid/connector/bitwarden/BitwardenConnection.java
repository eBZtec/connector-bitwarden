/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.api.ApiConnectionHandler;
import br.tec.ebz.connid.connector.bitwarden.services.CollectionsService;
import br.tec.ebz.connid.connector.bitwarden.services.GroupsService;
import br.tec.ebz.connid.connector.bitwarden.services.MembersService;
import org.identityconnectors.common.logging.Log;

import java.net.MalformedURLException;

public class BitwardenConnection extends ApiConnectionHandler {

    private static final Log LOG = Log.getLog(BitwardenConnection.class);

    private static final Class<MembersService> MEMBERS_SERVICE = MembersService.class;
    private static final Class<GroupsService> GROUPS_SERVICE = GroupsService.class;
    private static final Class<CollectionsService> COLLECTIONS_SERVICE = CollectionsService.class;

    private MembersService membersService;
    private GroupsService groupsService;
    private CollectionsService collectionsService;

    private BitwardenConfiguration configuration;

    public BitwardenConnection(BitwardenConfiguration configuration) throws MalformedURLException {
        super(configuration);
    }

    public void setupServices() throws MalformedURLException {
        membersService = setupClient(MEMBERS_SERVICE);
        groupsService = setupClient(GROUPS_SERVICE);
        collectionsService = setupClient(COLLECTIONS_SERVICE);
        LOG.ok("API services initialized successfully.");
    }

    public MembersService getMembersService() {
        return membersService;
    }

    public GroupsService getGroupsService() {
        return groupsService;
    }

    public CollectionsService getCollectionsService() {
        return collectionsService;
    }

    public void dispose() {
        this.configuration = null;
        membersService = null;

        LOG.ok("Connector disposed successfully.");
    }
}