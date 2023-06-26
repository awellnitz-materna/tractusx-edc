/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.edc.tests.negotiation;

import okhttp3.mockwebserver.MockWebServer;
import org.eclipse.edc.junit.annotations.EndToEndTest;
import org.eclipse.tractusx.edc.lifecycle.ParticipantRuntime;
import org.eclipse.tractusx.edc.token.KeycloakDispatcher;
import org.eclipse.tractusx.edc.token.MiwDispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.MIW_PLATO_PORT;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.MIW_SOKRATES_PORT;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.OAUTH_PORT;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.PLATO_BPN;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.PLATO_DSP_CALLBACK;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.PLATO_NAME;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.SOKRATES_BPN;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.SOKRATES_DSP_CALLBACK;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.SOKRATES_NAME;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.platoSsiConfiguration;
import static org.eclipse.tractusx.edc.lifecycle.TestRuntimeConfiguration.sokratesSsiConfiguration;

@EndToEndTest
public class SsiContractNegotiationInMemoryTest extends AbstractContractNegotiateTest {
    public static final String SUMMARY_VC_TEMPLATE = "summary-vc-no-dismantler.json";

    @RegisterExtension
    protected static final ParticipantRuntime PLATO_RUNTIME = new ParticipantRuntime(
            ":edc-tests:runtime:runtime-memory-ssi",
            PLATO_NAME,
            PLATO_BPN,
            platoSsiConfiguration()
    );

    @RegisterExtension
    protected static final ParticipantRuntime SOKRATES_RUNTIME = new ParticipantRuntime(
            ":edc-tests:runtime:runtime-memory-ssi",
            SOKRATES_NAME,
            SOKRATES_BPN,
            sokratesSsiConfiguration()
    );
    MockWebServer miwSokratesServer;
    MockWebServer miwPlatoServer;
    MockWebServer oauthServer;


    @BeforeEach
    void setup() throws IOException {
        miwSokratesServer = new MockWebServer();
        miwPlatoServer = new MockWebServer();
        oauthServer = new MockWebServer();

        miwSokratesServer.start(MIW_SOKRATES_PORT);
        miwSokratesServer.setDispatcher(new MiwDispatcher(SOKRATES_BPN, SUMMARY_VC_TEMPLATE, PLATO_DSP_CALLBACK));

        miwPlatoServer.start(MIW_PLATO_PORT);
        miwPlatoServer.setDispatcher(new MiwDispatcher(PLATO_BPN, SUMMARY_VC_TEMPLATE, SOKRATES_DSP_CALLBACK));

        oauthServer.start(OAUTH_PORT);
        oauthServer.setDispatcher(new KeycloakDispatcher());
    }

    @AfterEach
    void teardown() throws IOException {
        miwSokratesServer.shutdown();
        miwPlatoServer.shutdown();
        oauthServer.shutdown();
    }
}