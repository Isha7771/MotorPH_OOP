/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.oopmotorph.deductions.repository;

import com.mycompany.oopmotorph.deductions.model.DeductionItem;

import java.util.List;

public interface ContributionTableRepository {
    List<DeductionItem> load();
}