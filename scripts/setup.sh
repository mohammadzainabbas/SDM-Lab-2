#!/bin/bash
#====================================================================================
# Author: Mohammad Zain Abbas
# Date: 29th April, 2022
#====================================================================================
# This script is used to set up the enviorment & installations
#====================================================================================

# Enable exit on error
set -e -u -o pipefail

log () {
    echo "[[ log ]] $1"
}

error () {
    echo "[[ error ]] $1"
}

#Function that shows usage for this script
function usage()
{
cat << HEREDOC
Setup for SDM Lab 2 @ UPC
Usage: 
    
    $progname [OPTION] [Value]
Options:
    -h, --help              Show usage
Examples:
    $ $progname
    ⚐ → Installs all dependencies for your SDM Lab 2 
HEREDOC
}

progname=$(basename $0)
env_name='pyspark_env'

#Get all the arguments and update accordingly
while [[ "$#" -gt 0 ]]; do
    case $1 in
        -h|--help)
        usage
        exit 1
        ;;
        *) printf "\n$progname: invalid option → '$1'\n\n⚐ Try '$progname -h' for more information\n\n"; exit 1 ;;
    esac
    shift
done

install_brew() {
    if [ ! $(type -p brew) ]; then
        error "'brew' not found. Installing it now ..."
        /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    else
        log "'brew' found ..."
    fi
}

install_apache_spark() {
    if [ ! $(type -p spark-submit) ]; then
        error "'apache-spark' not found. Installing it now ..."
        brew install apache-spark
    else
        log "'apache-spark' found ..."
    fi
}

install_mvn() {
    if [ ! $(type -p mvn) ]; then
        error "'mvn' not found. Installing it now ..."
        brew install mvn
    else
        log "'mvn' found ..."
    fi
}

conda_init() {
    conda init --all || error "Unable to conda init ..."
    if [[ $SHELL == *"zsh"* ]]; then
        . ~/.zshrc
    elif [[ $SHELL == *"bash"* ]]; then
        . ~/.bashrc
    else
        error "Please restart your shell to see effects"
    fi
}

install_conda() {
    if [ ! $(type -p conda) ]; then
        error "'anaconda' not found. Installing it now ..."
        brew install --cask anaconda && conda_init
    else
        log "'anaconda' found ..."
    fi
}

create_conda_env() {
    conda create -n $env_name python=3.8 pandas -y || error "Unable to create new env '$env_name' ..."
    conda activate $env_name &> /dev/null || echo "" > /dev/null
    pip install pyspark > /dev/null
}

log "Starting Setup Service"

install_brew
install_apache_spark
install_mvn
install_conda
create_conda_env

log "All done !!"