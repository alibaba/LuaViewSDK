GIT_MERGE="git merge $1"

git pull
git checkout develop
git pull
$GIT_MERGE
git push
git push https://github.com/alibaba/LuaViewSDK develop:develop


git checkout master
git pull
git merge develop
git push
git push https://github.com/alibaba/LuaViewSDK master:master

