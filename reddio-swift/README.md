# reddio-swift

This directory contains the source codes and scripts to build the native library: `ReddioCrypto.xcframework`.

It's **NOT** recommend to use `ReddioCrypto.xcframework` directly, please use <https://github.com/reddio-com/ReddioKit> as instead.

## Build

Just execute:

```bash
make
```

The building process requires Xcode to package the artifact, so macOS is required. It would build the `ReddioCrypto.xcframework` for both iOS and MacOS.

The artifact would be located at `output/ReddioCrypto.xcframework`.
