GIT_MERGE="git merge $1"

GIT_CURRENT_BRANCH=$(git symbolic-ref -q HEAD)
GIT_CURRENT_BRANCH=${GIT_CURRENT_BRANCH##refs/heads/}
GIT_CURRENT_BRANCH=${GIT_CURRENT_BRANCH:-HEAD}

echo $GIT_CURRENT_BRANCH

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

git checkout $GIT_CURRENT_BRANCH

