GIT_MERGE="git merge $1"
GIT_CURRENT_BRANCH="git rev-parse --abbrev-ref HEAD"

CURRENT_BRANCH=$GIT_CURRENT_BRANCH

echo $CURRENT_BRANCH

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

git checkout $CURRENT_BRANCH
$GIT_CURRENT_BRANCH

