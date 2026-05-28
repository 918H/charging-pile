package com.charging.charging.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.core.response.R;
import com.charging.charging.entity.ChargingPile;
import com.charging.charging.service.ChargingPileService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/pile")
public class ChargingPileController {

    @Resource
    private ChargingPileService chargingPileService;

    @GetMapping("/list")
    public Result<List<ChargingPile>> list() {
        List<ChargingPile> list = chargingPileService.getList();
        return R.ok(list);
    }

    @GetMapping("/page")
    public Result<Page<ChargingPile>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ChargingPile> page = chargingPileService.getPage(current, size);
        return R.ok(page);
    }

    @GetMapping("/{pileId}")
    public Result<ChargingPile> detail(@PathVariable Long pileId) {
        ChargingPile pile = chargingPileService.getById(pileId);
        if (pile == null) {
            return R.fail("充电桩不存在");
        }
        return R.ok(pile);
    }

    @PostMapping
    public Result<Boolean> add(@RequestBody ChargingPile pile) {
        boolean success = chargingPileService.save(pile);
        return R.ok(success);
    }

    @PutMapping("/{pileId}")
    public Result<Boolean> update(@PathVariable Long pileId, @RequestBody ChargingPile pile) {
        pile.setPileId(pileId);
        boolean success = chargingPileService.update(pile);
        return R.ok(success);
    }

    @DeleteMapping("/{pileId}")
    public Result<Boolean> delete(@PathVariable Long pileId) {
        boolean success = chargingPileService.delete(pileId);
        return R.ok(success);
    }

    @GetMapping("/nearby")
    public Result<List<ChargingPile>> nearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "10") Double radius
    ) {
        List<ChargingPile> list = chargingPileService.getNearby(latitude, longitude, radius);
        return R.ok(list);
    }

    @GetMapping("/available")
    public Result<List<ChargingPile>> available() {
        List<ChargingPile> list = chargingPileService.getAvailable();
        return R.ok(list);
    }
}
