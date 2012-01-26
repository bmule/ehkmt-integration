#!/bin/bash

./clean-repository.bash

./daily-living-step10-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./daily-living-step8-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./medication-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./medications-step8-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./problem-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./problems-step10-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./problems-step11-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./problems-step14-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./problems-step6-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./problems-step8-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./vital-sign-client-example.bash
tail target/log.out
read -p "Press [Enter] to continue..."

./vital-sign-step11-client-example.bash
tail target/log.out
