to run integration tests
1. You have to run signaling server (with spring or without spring)
2. Server **MUST** be run without ssl (http) on port **8080**
3. Application **MUST** be deployed under ROOT with `/signaling` as a websocket endpoint (`ws://localhost:8080/signaling`)
3. Then run: `mvn clean test -Dtest-groups=integration`