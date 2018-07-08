package com.liaoliao.message.module;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import com.melink.bqmmplugin.rc.baseframe.utils.DensityUtils;
import com.melink.bqmmplugin.rc.bqmmsdk.bean.BQMMGif;
import com.melink.bqmmplugin.rc.bqmmsdk.widget.c;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class b extends Adapter<b.bn> {
    private final List<BQMMGif> a = new ArrayList();
    private b.a b;

    public b() {
    }

    @Override
    public b.bn onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout var3 = new LinearLayout(parent.getContext());
        var3.setLayoutParams(new LayoutParams(-2, -1));
        var3.setBackgroundColor(-1);
        var3.setPadding(DensityUtils.dip2px(5.0F), DensityUtils.dip2px(7.5F), DensityUtils.dip2px(5.0F), DensityUtils.dip2px(7.5F));
        c var4 = new c(parent.getContext());
        var3.addView(var4);
        return new b.bn(var3, var4);
    }

    @Override
    public void onBindViewHolder(b.bn var1, int var2) {
        final BQMMGif var3 = (BQMMGif)this.a.get(var2);
        int var4 = DensityUtils.dip2px(80.0F);
        if (!TextUtils.isEmpty(var3.getGif_thumb())) {
            var1.b.a(var3.getSticker_id(), var3.getGif_thumb(), var4, var4, var3.getIs_gif() == 1);
        } else if (!TextUtils.isEmpty(var3.getThumb())) {
            var1.b.a(var3.getSticker_id(), var3.getThumb(), var4, var4, false);
        } else {
            var1.b.b(var3.getSticker_id(), var3.getSticker_url(), var4, var4, var3.getIs_gif() == 1);
        }

        var1.b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (b.this.b != null) {
                   b.this.b.a(var3);
                }

            }
        });
    }


    public int getItemCount() {
        return this.a.size();
    }

    public void a(b.a var1) {
        this.b = var1;
    }

    public void a(@Nullable Collection var1) {
        this.a.clear();
        this.b(var1);
    }

    public void b(@Nullable Collection var1) {
        if (var1 != null) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
                Object var3 = var2.next();
                if (var3 instanceof BQMMGif) {
                    this.a.add((BQMMGif)var3);
                }
            }
        }

        this.notifyDataSetChanged();
    }

    interface a {
        void a(BQMMGif var1);
    }

     class bn extends ViewHolder {
        LinearLayout a;
        c b;

         bn(LinearLayout var2, c var3) {
            super(var2);
            this.a = var2;
            this.b = var3;
        }
    }
}
