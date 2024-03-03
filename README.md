[TOC]

# Intro

Need to help a friend with obtaining the private key from [Multibit](https://github.com/Multibit-Legacy/multibit), so that I count import it into Electrum,
but it seems like [Multibit](https://github.com/Multibit-Legacy/multibit) stopped the private key in some binary format which is not compatable with Electrum.

So I write this little piece of software to decrypt the private key and return it in a format which could be inserted in Electrum v4.5.3


# How to use it.

You need Java (openjdk 11) and maybe you need to change the variable in `PATH_TO_JAVAC` in `build-and-run.sh`
to point to the `bin` folder of your Java environment.

Otherwise, just run `build-and-run.sh` and answer the 2 questions and
you should get the unencrypted private key `XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX`.

```text
Private Key (Base64): XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 2014-01-01T01:01:01Z
```
