# Vanilla Modpack Loader

Vanilla Modpack Loader is some kind of new modpack thingy I made, 
it uses Toml files to create Modpacks.

**INSTALLING A MODPACK WILL CLEAR YOUR MODS FOLDER**

## HOW DO I MODPACK???

Create a .toml file and use this example:

```toml
[modpack.loader]
name = "forge" #forge, fabric, quilt #mandatory
mc_version = "1.19.3" #minecraft version #mandatory
version = "44.1.23" # the loader version #mandatory

[modpack.modZip] #optional
zip = "link/to.zip" # DIRECT link to zip

[modpack.mods] #option
mods = ["link/to/mod.jar", "another/mod.jar"] # DIRECT link to mod
```

## Important for Zip

Make sure the mods aren't in a folder in the zip!

## Info

If you chose forge, then the installer will show from forge.

If you use fabric or quilt, this will not happen.

## Compile it yourself

You need Java 17

To build run: ``gradlew build``

### Credits

- [Toml4j](https://github.com/mwanji/toml4j)
- [Zip4j](https://github.com/srikanth-lingala/zip4j)