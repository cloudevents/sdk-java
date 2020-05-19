openssl aes-256-cbc -K $encrypted_3210c925a91b_key \
       -iv $encrypted_3210c925a91b_iv \
       -in .travis.secring.enc -out .travis.secring -d

gpg2 --import .travis.pubring

gpg2 --batch --allow-secret-key-import --import .travis.secring

export GPG_TTY=$(tty)

mvn clean deploy -Drelease -DskipTests \
    --settings .travis.settings.xml \
    -Dgpg.executable=gpg2 \
    -Dgpg.keyname=4F144A60ECA44F0D \
    -Dgpg.passphrase=$PASSPHRASE
