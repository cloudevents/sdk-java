openssl aes-256-cbc -K $encrypted_3210c925a91b_key \
       -iv $encrypted_3210c925a91b_iv \
       -in .travis.secring.enc -out .travis.secring -d

#gpg --keyring=$TRAVIS_BUILD_DIR/pubring.gpg \
#     --no-default-keyring \
#     --import .travis.pubring

gpg2 --version

gpg2 --import .travis.pubring

#gpg --allow-secret-key-import --keyring=$TRAVIS_BUILD_DIR/secring.gpg \
#     --no-default-keyring \
#     --import .travis.secring

gpg2 --allow-secret-key-import --import .travis.secring

#mvn clean deploy -P release -DskipTests \
#    --settings .travis.settings.xml \
#    -Dgpg.executable=gpg2 \
#    -Dgpg.passphrase=$PASSPHRASE \
#    -Dgpg.publicKeyring=$TRAVIS_BUILD_DIR/pubring.gpg \
#    -Dgpg.secretKeyring=$TRAVIS_BUILD_DIR/secring.gpg

mvn package org.apache.maven.plugins:maven-gpg-plugin:1.6:sign -DskipTests \
    -Dgpg.executable=gpg2 \
    -Dgpg.passphrase=$PASSPHRASE
