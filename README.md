# LSynchronizer
A tool to keep directories in sync and transfer changes.
It is not a background proccess meaning it is neccessary to run the program every time you want them to be synchronized. When starting the first time on two directories all files not existing in the other directory are copied. In all later synchronization runnings the changes will be detected with a list stored by the program automatically and are applied in both directories. If the same file in both directories got changed the file with the newest changing date is used.
