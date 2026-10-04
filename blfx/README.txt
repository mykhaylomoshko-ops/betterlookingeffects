Betterlooking Effects  (Fabric, Minecraft 1.21.11)

GET THE JAR (no installs needed)
1. Make a free GitHub repo, upload this whole folder (keep .github/workflows/build.yml).
2. Open the repo's Actions tab -> "build" -> when green, download the artifact
   "betterlookingeffects-jar" (a zip containing betterlookingeffects-1.2.0.jar).
3. Put the jar in .minecraft/mods together with Fabric Loader 0.18.4+ and Fabric API 0.141.1+1.21.11.

OR build locally: JDK 21 + Gradle 8.14+, run `gradle build` -> build/libs/.

USE
Right Shift opens the GUI (rebind in Controls). Every effect in the game is listed (pages).
Button cycles Shown -> Hidden -> Forced. Forced = display it even if you don't have it
(that's how you can show instant effects like Saturation). Level/Duration boxes: blank = real.
Config: config/betterlookingeffects.json
