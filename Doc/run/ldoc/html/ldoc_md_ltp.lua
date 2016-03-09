return [[
> local lev = ldoc.level or 2
> local lev1,lev2 = ('#'):rep(lev),('#'):rep(lev+1)
> for kind, items in module.kinds() do
>  local kitem = module.kinds:get_item(kind)
>  if kitem then
$(lev1) $(ldoc.descript(kitem))

>  end
>   for item in items() do
$(lev2) $(ldoc.display_name(item))

$(ldoc.descript(item))

> end
> end
]]
