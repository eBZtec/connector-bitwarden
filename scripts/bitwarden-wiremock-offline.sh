#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WIREMOCK_JAR="${WIREMOCK_JAR:-$ROOT_DIR/tools/wiremock-standalone-3.13.2.jar}"

API_DIR="$ROOT_DIR/src/test/resources/wiremock/bitwarden-api"
AUTH_DIR="$ROOT_DIR/src/test/resources/wiremock/bitwarden-auth"

API_PORT=18081
AUTH_PORT=18082

export BW_BASE_URL="http://localhost:${API_PORT}"
export BW_AUTH_URL="http://localhost:${AUTH_PORT}"

echo "Running Bitwarden tests in OFFLINE mode via WireMock..."
echo "  WireMock JAR: $WIREMOCK_JAR"
echo "  API  WireMock: $BW_BASE_URL (root-dir: $API_DIR)"
echo "  AUTH WireMock: $BW_AUTH_URL (root-dir: $AUTH_DIR)"
echo

java -jar "$WIREMOCK_JAR" \
  --port "$API_PORT" \
  --root-dir "$API_DIR" \
  &
API_PID=$!

java -jar "$WIREMOCK_JAR" \
  --port "$AUTH_PORT" \
  --root-dir "$AUTH_DIR" \
  &
AUTH_PID=$!

cleanup() {
  echo
  echo "Stopping WireMock processes..."
  kill "$API_PID" "$AUTH_PID" 2>/dev/null || true
}
trap cleanup EXIT

echo "Running Maven tests (OFFLINE playback)..."
echo "  Command: mvn test $*"
echo

cd "$ROOT_DIR"
mvn clean verify "$@"

echo
echo "Offline run finished (no real Bitwarden calls)."
