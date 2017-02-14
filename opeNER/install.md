# OpeNER platform install guide

This is a quick, updated, custom-made install guide for the OpeNER components used in our architecture.

## Prerequisites 

* Any Linux / Unix operating system. Tested successfully under Ubuntu 14.10 and 15.09
* Ruby 2.+
* JRuby 1.7.9+
* Python 2.6+
* Java 1.7+
* Perl 5+
* libarchive development headers

### Install Ruby and JRuby

```
sudo apt-get install ruby jruby ruby-dev

# install rvm securely from remote git repository
gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
curl -sSL https://get.rvm.io | sudo bash -s stable
echo "source $HOME/.rvm/scripts/rvm" >> ~/.bash_profile
sudo apt-get git

# to execute rvm using the sudo command set the following flag and use rvmsudo hence forth
export rvmsudo_secure_path=1

rvmsudo rvm get head
rvmsudo rvm reload

# update the juby environment to the latest version
# you should remember the jruby version in order to use it in the last step

rvmsudo rvm install 2.3.1
rvmsudo rvm install jruby
rvmsudo rvm use jruby-x.x.x.x
```

### Install Python (2.6+)

```
sudo apt-get install python
```
### Install Pip for root environment

To be able to execute pip in a sudo environment

```
sudo apt-get install python-pip
```

### Install Oracle Java

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer
```

### Install Perl

```
sudo apt-get install perl
```

### Install extra xml packages

```
sudo apt-get install libxml2-dev libxslt-dev python-dev zlibc liblzma-dev zlib1g-dev python3-lxml python-lxml

pip install lxml
```

### Crypto libraries

```
sudo apt-get install libssl-dev
```

### Archive Development libraries

```
sudo apt-get install libarchive-dev
```

### Install Python Virtual Environment

Complete guide: https://github.com/brainsik/virtualenv-burrito

## Update environment settings

```
mkvirtualenv opener
workon opener
```

## Install gem component name

```
rvmsudo gem install bundler

rvmsudo gem install opener-language-identifier
rvmsudo gem install opener-tokenizer
rvmsudo gem install opener-pos-tagger

# Optional
rvmsudo gem install opener-tree-tagger 
```

Note: If the opener-tree-tagger component fails to install, you can try to install the pip targets manually:

```
sudo pip install https://github.com/opener-project/VU-kaf-parser/archive/master.zip#egg=VUKafParserPy
```

## Test if the install has been successful

```
echo "This is absolutelty great" | language-identifier | tokenizer | pos-tagger
``` 

## What's next
Now, that you have completely installed OpenNER, it is time to start using its services for [WatchMeRest API](WatchMeRest/README.md), but before you use this API, consider reading [WatchMeDictionaryParser documentation](WatchMeDictionaryParser/README.md) for instructions on how to create your own Language Dictionary
