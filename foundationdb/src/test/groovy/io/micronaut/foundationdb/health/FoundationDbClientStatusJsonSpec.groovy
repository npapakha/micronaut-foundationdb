package io.micronaut.foundationdb.health

import io.micronaut.foundationdb.AbstractFoundationDbSpec
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Unroll


@MicronautTest
class FoundationDbClientStatusJsonSpec extends AbstractFoundationDbSpec {

    @Inject
    private JsonMapper jsonMapper

    @Unroll
    def 'should deserialize clientStatus'() {
        when:
        def clientStatus = jsonMapper.readValue(json, FoundationDbClientStatus.class)

        then:
        clientStatus.healthy == healthy
        clientStatus.clusterId == clusterId
        clientStatus.connections[0].status == status
        clientStatus.connections[0].address == address

        where:
        json                | healthy   | clusterId                             | address           | status
        CLIENT_STATUS_UP    | true      | 'ede056c4517112ded5fbb30a190e01dd'    | '127.0.0.1:52997' | 'connected'
        CLIENT_STATUS_DOWN  | false     | '8998052a62673e2026ca6fbefcfaae8d'    | '127.0.0.1:54081' | 'failed'
    }

    static def CLIENT_STATUS_UP =
        """
        {
          "ClusterID": "ede056c4517112ded5fbb30a190e01dd",
          "CommitProxies": [
            "127.0.0.1:52997"
          ],
          "Connections": [
            {
              "Address": "127.0.0.1:52997",
              "BytesReceived": 716,
              "BytesSampleTime": 0.019739151000976562,
              "BytesSent": 888,
              "Compatible": true,
              "ConnectFailedCount": 0,
              "LastConnectTime": 0.019739151000976562,
              "PingCount": 0,
              "PingTimeoutCount": 0,
              "ProtocolVersion": "fdb00b074000000",
              "Status": "connected"
            }
          ],
          "Coordinators": [
            "127.0.0.1:52997"
          ],
          "CurrentCoordinator": "127.0.0.1:52997",
          "GrvProxies": [
            "127.0.0.1:52997"
          ],
          "Healthy": true,
          "NumConnectionsFailed": 0,
          "StorageServers": []
        }
        """

    static def CLIENT_STATUS_DOWN =
        """
        {
          "ClusterID": "8998052a62673e2026ca6fbefcfaae8d",
          "CommitProxies": [
            "127.0.0.1:54081"
          ],
          "Connections": [
            {
              "Address": "127.0.0.1:54081",
              "BytesReceived": 716,
              "BytesSampleTime": 1.2494585514068604,
              "BytesSent": 888,
              "Compatible": true,
              "ConnectFailedCount": 2,
              "LastConnectTime": 0.17511653900146484,
              "PingCount": 0,
              "PingTimeoutCount": 0,
              "Status": "failed"
            }
          ],
          "Coordinators": [
            "127.0.0.1:54081"
          ],
          "CurrentCoordinator": "127.0.0.1:54081",
          "GrvProxies": [
            "127.0.0.1:54081"
          ],
          "Healthy": false,
          "NumConnectionsFailed": 1,
          "StorageServers": []
        }
        """
}
