# Liahtina Test Datapack
# Scheduled by test:main, runs after 5s delay
# Usage: /function test:delayed

say "[test:delayed] Triggered successfully! /schedule + /function working properly"
tellraw @a {"text":"[schedule+function] 5-second timer triggered successfully!","color":"green"}
