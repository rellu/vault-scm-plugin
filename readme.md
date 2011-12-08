SourceGear Vault (tm) plugin for Jenkins
====================


This is a plugin for [Jenkins CI server](http://jenkins-ci.org/). This plugin integrates [SourceGear Vault/Fortress](http://www.sourcegear.com/) version control with Jenkins. Currently the plugin supports polling SCM for changes, triggering build if there is changes and keeping the changelog.

NOTE
====

* This is version 0.1 and might still have some unexpected issues.
* We are not affiliated with SourceGear so in this sense this is an unofficial plugin.
* This software is MIT licensed open-source, so please free to fork it and improve it :)

Installation
============

* Copy the .hpi file to <jenkins>/plugins/ or use the GUI installer on "manage Jenkins->manage plugins->advanced"
* Restart Jenkins

Alternatively you can built the project from sources with Maven.

Note: you need to have vault command line tool installed on Jenkins machine to use this plugin.
	
Usage
=====

After the plugin is installed, you should see Vault SCM option when you choose and SCM trigger for a job.

* Path to Vault executable: fill here path to vault.exe e.g. C:\Program Files\SourceGear\Fortress\Vault.exe
* Server name: e.g. 10.10.10.1 (optional)
* Username: username for vault (optional)
* Password: password for vault (optional)
* Repository: repository name
* Path: folder path in vault. e.g. $/somefolder. This needs to always start with $/ and the plugin checks changes in this given folder only (recursively)
* SSL: tick this if you need to use SSL.
* Merge type: automatic, later or overwrite i.e. how files are merged.

more information about the parameters [here](http://download.sourcegear.com/misc/vault/help/client/clc.htm). See VERSIONHISTORY and GET commands.

NOTE: passwords, usernames etc. are not encrypted in any way. To overcome this problem you can execute vault.exe REMEMBERLOGIN -host -user -password with your details on the same user that Jenkins use and then leave those fields empty. This way Vault will remember your login details. 	

Functionality
=============

This plugin utilizes Vault's command line tool. VERSIONHISTORY command is used to determine and log changes and GET command is used to retrieve files from SCM server. 

TODO, known issues and limitations:
=====

* When you configure the plugin the merge option dropdown always defaults to automatic
* Changelog does not display modified filenames
* Changelog version number is calculated based on folder name


LICENSE
=======

MIT License

Copyright (c) 2011 Antti Relander / Eficode

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


