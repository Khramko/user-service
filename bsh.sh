#!/usr/bin/env bash
# fix-review-part2.sh — добить хвосты после основного скрипта
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

log() { printf "\033[1;34m[fix2]\033[0m %s\n" "$*"; }

# 1. application.yaml
log "1. Правлю logging.level в application.yaml"
sed -i 's/com\.example\.health_service/com.example.user_service/g' \
  src/main/resources/application.yaml

# 2. Ingress — добавляю /api/profile
log "2. Добавляю маршрут /api/profile в Ingress"
python3 - <<'PY'
import pathlib
p = pathlib.Path("k8s/ingress.yaml")
t = p.read_text()
if "/api/profile" not in t:
    anchor = """          - path: /auth/.*
            pathType: ImplementationSpecific
            backend:
              service:
                name: user-service
                port:
                  number: 8000
"""
    addition = """          - path: /api/profile
            pathType: Prefix
            backend:
              service:
                name: user-service
                port:
                  number: 8000
          - path: /api/profile/.*
            pathType: ImplementationSpecific
            backend:
              service:
                name: user-service
                port:
                  number: 8000
"""
    t = t.replace(anchor, addition + anchor)
    p.write_text(t)
PY

# 3. README — таблица API
log "3. Обновляю таблицу API в README.md"
python3 - <<'PY'
import re, pathlib
p = pathlib.Path("README.md")
t = p.read_text()

old_table = re.search(
    r"\| Метод \| Путь \| Описание \|\n\|---\|---\|---\|\n(?:\|.*\|\n)+",
    t,
)
new_table = """| Метод | Путь | Описание | Auth |
|---|---|---|---|
| GET | `/health` | Liveness/readiness проба | — |
| GET | `/actuator/prometheus` | Метрики Prometheus | — |
| POST | `/auth/register` | Регистрация | — |
| POST | `/auth/login` | Вход, выдаёт JWT | — |
| GET | `/api/profile` | Свой профиль | Bearer |
| PUT | `/api/profile` | Обновить свой профиль | Bearer |
| GET | `/api/profile/{id}` | Профиль по id (только свой, иначе 403) | Bearer |
| PUT | `/api/profile/{id}` | Обновить профиль по id (только свой) | Bearer |

> `/api/users/**` — legacy CRUD, закрыт для обычных пользователей (`denyAll`).
> Коллекция `etc/collection.json` помечена как deprecated.
"""
if old_table:
    t = t.replace(old_table.group(0), new_table)
else:
    # fallback: дописать после заголовка "## API Эндпоинты"
    t = t.replace("## API Эндпоинты\n", "## API Эндпоинты\n\n" + new_table + "\n")
p.write_text(t)
PY

# 4. .idea — чистим мёртвые ссылки
log "4. Чищу .idea от остатков health-service"
if [[ -f .idea/compiler.xml ]]; then
  sed -i '/<module name="health-service"/d' .idea/compiler.xml
fi
if [[ -f .idea/workspace.xml ]]; then
  python3 - <<'PY'
import re, pathlib
p = pathlib.Path(".idea/workspace.xml")
t = p.read_text()
t = re.sub(r'\s*<configuration name="HealthServiceApplication".*?</configuration>', '', t, flags=re.S)
t = t.replace('selected="Spring Boot Application.HealthServiceApplication"',
              'selected="Spring Boot Application.UserServiceApplication"')
t = t.replace('/home/lone-digger/OpenIDEProjects/health-service',
              '/home/lone-digger/OpenIDEProjects/user-service')
t = t.replace('&quot;health_service&quot;', '&quot;user_service&quot;')
t = t.replace('&quot;health-service&quot;', '&quot;user-service&quot;')
p.write_text(t)
PY
fi

# 5. Чистая пересборка
log "5. mvn clean package"
if command -v mvn >/dev/null 2>&1; then
  mvn -q clean package -DskipTests
else
  warn "mvn не найден — соберите вручную"
fi

# 6. Финальная проверка
log "6. Финальный grep"
echo "--- health_service в src (ожидаем пусто) ---"
grep -rn "health_service" src/ 2>/dev/null || echo "  (пусто)"
echo "--- health-service в конфигах (ожидаем пусто) ---"
grep -rn "health-service" \
  --include='*.yaml' --include='*.yml' --include='*.json' \
  --include='*.java' --include='*.md' --include='*.txt' \
  --include='Dockerfile' . 2>/dev/null | grep -v '/target/' | grep -v '/.git/' || echo "  (пусто)"
echo "--- /api/profile в Ingress (ожидаем 2 строки) ---"
grep -n "/api/profile" k8s/ingress.yaml || echo "  (пусто — ПРОБЛЕМА)"

# 7. Коммит
log "7. Коммит"
git add -A
git commit -m "fix(review): application.yaml logging, ingress /api/profile, README API table, .idea cleanup" || \
  warn "Нечего коммитить"