openssl aes-256-cbc -K $encrypted_3210c925a91b_key -iv $encrypted_3210c925a91b_iv -in .travis.gpg -out .travis.asc -d

gpg2 --keyring=$TRAVIS_BUILD_DIR/pubring.gpg --no-default-keyring --import .travis.asc
gpg2 --allow-secret-key-import --keyring=$TRAVIS_BUILD_DIR/secring.gpg --no-default-keyring --import .travis.asc
mvn clean deploy -P release -DskipTests --settings .travis.settings.xml -Dgpg.executable=gpg2 -Dgpg.keyname=349076B63A645A5DCD27614D48C38075433E04B2 -Dgpg.passphrase=$PASSPHRASE -Dgpg.publicKeyring=$TRAVIS_BUILD_DIR/pubring.gpg -Dgpg.secretKeyring=$TRAVIS_BUILD_DIR/secring.gpg
