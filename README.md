<div align="center">

# ⚽ Beticon

**Call the score. Play your Joker. Beat your friends.**

A prediction game for football tournaments: bet on every scoreline, collect Points when the final whistle blows, and climb the leaderboard.

[![Frontend](https://github.com/NoOneHardy/beticon/actions/workflows/frontend.yml/badge.svg?branch=develop)](https://github.com/NoOneHardy/beticon/actions/workflows/frontend.yml)
[![Service](https://github.com/NoOneHardy/beticon/actions/workflows/service.yml/badge.svg?branch=develop)](https://github.com/NoOneHardy/beticon/actions/workflows/service.yml)
[![License: GPL v3](https://img.shields.io/badge/license-GPLv3-blue.svg)](LICENSE)
<br>
![Angular](https://img.shields.io/badge/Angular-22-DD0031?logo=angular&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.2-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)

[How you score](#-how-you-score) · [Host your own tournament](#%EF%B8%8F-host-your-own-tournament) · [Development](#%EF%B8%8F-development) · [Contributing](#-contributing)

</div>

---

## 🏆 What is Beticon?

Beticon was born for the **FIFA World Cup 2026** and is now being prepared for the tournaments still to come.

Before every Game, each player places a **Bet**: the scoreline they think the Game will end with. When the Result is in, Beticon turns every Bet into **Points**, and the leaderboard shows who saw it coming.

- 🎯 **Bet** on the scoreline of every Game in the tournament
- 🃏 **Play Jokers** to double or triple the Points of the Bets you're sure about
- 📊 **Follow the leaderboard** and see where your Points came from, Bet by Bet
- 🛠️ **Run the tournament** from the admin panel: teams, groups, Games, Results and players

> [!NOTE]
> 💰 **About money.** Beticon itself never handles money. A group can play for stakes, but for legal reasons that happens outside the app: the **Host** (the person who runs the tournament) collects the stakes and pays out the winners. Stakes are completely optional; bragging rights work just as well.

## 🎯 How you score

Each Bet is compared with the Game's **Result**, the score at the end of play including extra time. If a knockout Game is still level after extra time, the winner of the penalty shoot-out is credited with one extra goal, so a knockout Result is never a tie.

| You got right…                        | Points |
|---------------------------------------|:------:|
| 🥇 The winner (or that it's a tie)    | **5**  |
| 🏠 The home team's goals              | **2**  |
| ✈️ The guest team's goals             | **2**  |
| ➕ The total number of goals           | **1**  |
| 💯 **Exact Bet**: everything above    | **10** |

🃏 **Jokers** multiply a Bet's Points by **×2** or **×3**. Every player gets a limited number of each per tournament, so play them wisely. A Joker on a Bet that earns nothing is a Wasted Joker.

<details>
<summary><b>📐 Worked example</b></summary>

<br>

The Result is **Switzerland 2 : 1 Brazil**.

| Bet   | Winner | Home | Guest | Total | Joker | Points     |
|-------|:------:|:----:|:-----:|:-----:|:-----:|:----------:|
| 2 : 1 | 5      | 2    | 2     | 1     | ×2    | **20** 🔥  |
| 3 : 1 | 5      | –    | 2     | –     | –     | **7**      |
| 1 : 2 | –      | –    | –     | 1     | ×3    | **3**      |
| 0 : 0 | –      | –    | –     | –     | ×3    | **0** 😬   |

</details>

Beticon's full vocabulary lives in [`GLOSSARY.md`](GLOSSARY.md).

## 🏟️ Host your own tournament

Right now, hosting your own instance is the way to play Beticon. The [public instance](https://beticon-dev.no1hardy.ch) is a development preview, so you can't play a tournament on it.

Beticon runs as five containers: the Angular **frontend**, the Spring Boot **service**, **MySQL**, a small nginx **CDN** for uploaded images, and an nginx **proxy** that serves everything on port `26000`.

<details>
<summary><b>🐳 Run it with Docker Compose</b></summary>

<br>

1. **Build the images** from the repository root:

   ```bash
   docker build -f service/Dockerfile  -t beticon/service .
   docker build -f frontend/Dockerfile -t beticon/frontend .
   docker build -f proxy/Dockerfile    -t beticon/proxy .
   ```

2. **Point the compose file at your images.** In `docker-compose.prod.yaml`, replace each `docker.no1hardy.ch/noonehardy/beticon/...` image with the tag you just built, and remove the `pull_policy: always` lines.

3. **Create the three `.env` files:**

   | File           | What goes in                                                                                                                                                                                                |
   |----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
   | `mysql/.env`   | `MYSQL_ROOT_PASSWORD`, `MYSQL_USER`, `MYSQL_PASSWORD`                                                                                                                                                       |
   | `service/.env` | Copy [`service/.env.example`](service/.env.example). Use `DATABASE_HOST=beticon-mysql` and the MySQL user from above, generate a random `SECRET_KEY`, add SMTP credentials for the verification emails, and add `BASE_URL=<your public URL>` |
   | `proxy/.env`   | `BACKEND_HOST=http://beticon-service:26001`, `FRONTEND_HOST=http://beticon-frontend`, `CDN_HOST=http://beticon-cdn`                                                                                           |

4. **Start it:**

   ```bash
   docker compose -f docker-compose.prod.yaml up -d
   ```

   Beticon is now served on port `26000`. Put a reverse proxy with TLS in front of it before you invite anyone.

5. **Make yourself admin.** Register, verify your email, then set your account's `role` to `1` (`ADMIN`) in the `beticon-user` table. Roles are stored as numbers, so `USER` is `0`. From then on you can approve the other players from the admin panel.

</details>

**Tune the tournament** with the service's environment: `JOKER_DOUBLE` and `JOKER_TRIPLE` set the Joker limits (defaults 10 and 6). The participation fee behind the dashboard's jackpot is set in [`application.yml`](service/src/main/resources/application.yml).

## 🛠️ Development

Beticon is a Gradle monorepo:

```text
beticon/
├── frontend/   🅰️  Angular 22 · Material · NgRx · Vitest        (pnpm)
├── service/    🍃  Spring Boot 4.2 · Java 25 · JPA · JWT · JUnit 5
├── mysql/      🐬  database diagram
├── proxy/      🔀  nginx: routes /api to the service, /cdn to storage, the rest to the frontend
└── docs/adr/   📜  architecture decision records
```

<details>
<summary><b>🚀 Run it locally</b></summary>

<br>

You'll need **Java 25**, **Node 22** with **pnpm**, and **Docker**.

1. **Start MySQL, the CDN and the proxy.** Create `mysql/.env` with `MYSQL_USER` and `MYSQL_PASSWORD`, and `proxy/.env` with:

   ```dotenv
   BACKEND_HOST=http://localhost:26001
   FRONTEND_HOST=http://localhost:4200
   CDN_HOST=http://localhost:26009
   ```

   Then start the three containers:

   ```bash
   docker compose up -d mysql cdn proxy
   ```

2. **Start the service.** Copy `service/.env.example` to `service/.env` and fill it in (`DATABASE_HOST=localhost:26006`, plus SMTP settings; a local mail catcher such as [Mailpit](https://mailpit.axllent.org) works fine). Delete the inline `# …` comments while you're at it: `bootRun` reads the file as is and would keep them in the values. Then run:

   ```bash
   ./gradlew :service:bootRun --args='--spring.profiles.active=local'
   ```

3. **Start the frontend:**

   ```bash
   cd frontend && pnpm install && pnpm start
   ```

4. Open **http://localhost:26000** 🎉

</details>

<details>
<summary><b>✅ Lint and test</b></summary>

<br>

```bash
./gradlew :frontend:lint :frontend:test   # ESLint + Vitest
./gradlew :service:lint :service:test     # Spotless + JUnit 5 (integration tests on embedded H2)
./gradlew :service:spotlessApply          # auto-format the service
```

CI runs the same tasks on every push.

</details>

### 🤖 Built by humans, assisted by AI

Beticon is maintained by real developers. AI agents ([Claude Code](https://claude.com/claude-code)) support the work: design grilling, specs, implementation help and reviews. We follow [Matt Pocock's workflow and skills](https://github.com/mattpocock/skills): grill an idea, write the spec, then implement it in small reviewed steps.

## 🤝 Contributing

Contributions are welcome! Browse the [issues](https://github.com/NoOneHardy/beticon/issues): they're the roadmap. Want to run Beticon for your group and something's missing? Open an issue.

- 🌿 **Branches**: `<type>/<issue>-<slug>`, e.g. `feat/123-joker-history`, where `<type>` is a conventional commit type
- ✍️ **Commits**: [gitmoji](https://gitmoji.dev) prefix, short lowercase subject, issue reference, e.g. `✨ add joker history to the dashboard (#123)`
- 🔀 **Pull requests**: target `develop`, with a title prefixed by the branch type's emoji, e.g. `✨ Joker history on the dashboard`
- 📏 **Before you code**: read [`CODING_STANDARDS.md`](CODING_STANDARDS.md), the [`GLOSSARY.md`](GLOSSARY.md) and the decisions in [`docs/adr/`](docs/adr)

## 📄 License

Beticon is free software under the [GNU General Public License v3.0](LICENSE).

<div align="center">
<br>
<sub>Made with ⚽ and ☕ for every tournament to come.</sub>
</div>
