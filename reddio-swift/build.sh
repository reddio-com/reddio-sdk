#!/usr/bin/env bash

cd ../ && make
cd reddio-swift || exit
mkdir -p ../output/include/
cp include/module.modulemap ../output/include/

rustup target add aarch64-apple-darwin
rustup target add aarch64-apple-ios
rustup target add x86_64-apple-ios

cd ../ && \
    cargo build --release --manifest-path=reddio/Cargo.toml --target aarch64-apple-darwin && \
    cargo build --release --manifest-path=reddio/Cargo.toml --target aarch64-apple-ios && \
    cargo build --release --manifest-path=reddio/Cargo.toml --target x86_64-apple-ios

xcodebuild -create-xcframework \
    -library ./reddio/target/aarch64-apple-darwin/release/libreddio.a -headers ./output/include/ \
    -library ./reddio/target/aarch64-apple-ios/release/libreddio.a -headers ./output/include/ \
    -library ./reddio/target/x86_64-apple-ios/release/libreddio.a -headers ./output/include/ \
    -output output/ReddioCrypto.xcframework
