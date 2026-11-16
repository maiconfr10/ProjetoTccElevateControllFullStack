#!/usr/bin/env bash
# Para usar depois de iniciar a API no perfil demo (requer curl e python3)
set -euo pipefail
BASE="${BASE:-http://localhost:8080/api}"
JAR=$(mktemp)
trap 'rm -f "$JAR"' EXIT
printf 'Obtendo CSRF...\n'
CSRF_JSON=$(curl -fsS -c "$JAR" -b "$JAR" "$BASE/auth/csrf")
TOKEN=$(printf '%s' "$CSRF_JSON" | python3 -c 'import json,sys; print(json.load(sys.stdin)["token"])')
EMAIL="${DEMO_EMAIL:-camila@exemplo.com}"
PASSWORD="${DEMO_PASSWORD:-Demo@123}"
PAYLOAD=$(DEMO_EMAIL="$EMAIL" DEMO_PASSWORD="$PASSWORD" python3 -c 'import os,json;print(json.dumps({"email":os.environ["DEMO_EMAIL"],"password":os.environ["DEMO_PASSWORD"]}))')
printf 'Fazendo login...\n'
curl -fsS -c "$JAR" -b "$JAR" -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $TOKEN" -d "$PAYLOAD" "$BASE/auth/login"
printf '\nUsuario atual...\n'
curl -fsS -c "$JAR" -b "$JAR" "$BASE/auth/me"
printf '\nDashboard...\n'
curl -fsS -c "$JAR" -b "$JAR" "$BASE/dashboard/resumo"
printf '\nQuantidade de rotinas mensais...\n'
curl -fsS -c "$JAR" -b "$JAR" "$BASE/checklists/catalogo/mensal-2026" | python3 -c 'import json,sys;print(len(json.load(sys.stdin)),"rotinas")'
printf '\nConcluido.\n'
